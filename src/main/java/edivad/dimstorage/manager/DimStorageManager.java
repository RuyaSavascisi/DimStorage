package edivad.dimstorage.manager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import edivad.dimstorage.api.AbstractDimStorage;
import edivad.dimstorage.api.DimStoragePlugin;
import edivad.dimstorage.api.Frequency;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class DimStorageManager {

  private static final HashMap<String, DimStoragePlugin> plugins = new HashMap<>();
  private static DimStorageManager serverManager;
  private static DimStorageManager clientManager;
  private final boolean client;
  private final Map<String, AbstractDimStorage> storageMap;
  private final Map<String, List<AbstractDimStorage>> storageList;
  private final List<AbstractDimStorage> dirtyStorage;
  private File saveDir;
  private File[] saveFiles;
  private int saveTo;
  private CompoundTag saveTag;

  public DimStorageManager(boolean client) {
    this.client = client;

    storageMap = Collections.synchronizedMap(new HashMap<>());
    storageList = Collections.synchronizedMap(new HashMap<>());
    dirtyStorage = Collections.synchronizedList(new LinkedList<>());

    for (String key : plugins.keySet()) {
      storageList.put(key, new ArrayList<>());
    }

    if (isServer()) {
      load();
    }

  }

  public static void reloadManager(boolean client) {
    DimStorageManager newManager = new DimStorageManager(client);

    if (client) {
      clientManager = newManager;
    } else {
      serverManager = newManager;
    }
  }

  public static DimStorageManager instance(boolean client) {
    DimStorageManager manager = client ? clientManager : serverManager;
    if (manager == null) {
      reloadManager(client);
      manager = client ? clientManager : serverManager;
    }
    return manager;
  }

  public static void registerPlugin(DimStoragePlugin plugin) {
    plugins.put(plugin.identifier(), plugin);

    if (serverManager != null) {
      serverManager.storageList.put(plugin.identifier(), new ArrayList<>());
    }
    if (clientManager != null) {
      clientManager.storageList.put(plugin.identifier(), new ArrayList<>());
    }
  }

  public boolean isServer() {
    return !client;
  }

  private void sendClientInfo(Player player) {
    for (Map.Entry<String, DimStoragePlugin> plugin : plugins.entrySet()) {
      plugin.getValue().sendClientInfo(player, storageList.get(plugin.getKey()));
    }
  }

  private void load() {
    var minecraftServer = ServerLifecycleHooks.getCurrentServer().overworld().getServer();
    saveDir = new File(minecraftServer.getWorldPath(LevelResource.ROOT).toFile(), "DimStorage");
    try {
      if (!saveDir.exists()) {
        saveDir.mkdirs();
      }

      saveFiles = new File[]{new File(saveDir, "data1.dat"), new File(saveDir, "data2.dat"),
          new File(saveDir, "lock.dat")};

      if (saveFiles[2].exists() && saveFiles[2].length() > 0) {
        FileInputStream fin = new FileInputStream(saveFiles[2]);
        saveTo = fin.read() ^ 1;
        fin.close();

        if (saveFiles[saveTo ^ 1].exists()) {
          var din = new DataInputStream(new FileInputStream(saveFiles[saveTo ^ 1]));
          saveTag = NbtIo.readCompressed(din, NbtAccounter.unlimitedHeap());
          din.close();
        } else {
          saveTag = new CompoundTag();
        }
      } else {
        saveTag = new CompoundTag();
      }
    } catch (Exception e) {
      throw new RuntimeException(String.format(
          "DimStorage was unable to read it's data, please delete the 'DimStorage' folder Here: %s and start the server again.",
          saveDir), e);
    }
  }

  private void save(boolean force) {
    if (!dirtyStorage.isEmpty() || force) {
      for (AbstractDimStorage inv : dirtyStorage) {
        saveTag.put(inv.freq + ",type=" + inv.type(), inv.saveToTag());
        inv.setClean();
      }

      dirtyStorage.clear();

      try {
        File saveFile = saveFiles[saveTo];
        if (!saveFile.exists()) {
          saveFile.createNewFile();
        }

        DataOutputStream dout = new DataOutputStream(new FileOutputStream(saveFile));
        NbtIo.writeCompressed(saveTag, dout);
        dout.close();
        FileOutputStream fout = new FileOutputStream(saveFiles[2]);
        fout.write(saveTo);
        fout.close();
        saveTo ^= 1;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  public AbstractDimStorage getStorage(Frequency freq, String type) {
    String key = freq + ",type=" + type;
    AbstractDimStorage storage = storageMap.get(key);

    if (storage == null) {
      storage = plugins.get(type).createDimStorage(this, freq);

      if (!client && saveTag.contains(key)) {
        storage.loadFromTag(saveTag.getCompound(key));
      }

      storageMap.put(key, storage);
      storageList.get(type).add(storage);
    }
    return storage;
  }

  public void requestSave(AbstractDimStorage storage) {
    dirtyStorage.add(storage);
  }

  public static class DimStorageSaveHandler {

    @SubscribeEvent
    public void onWorldLoad(LevelEvent.Load event) {
      if (event.getLevel().isClientSide()) {
        reloadManager(true);
      }
    }

    @SubscribeEvent
    public void onWorldSave(LevelEvent.Save event) {
      if (!event.getLevel().isClientSide() && instance(false) != null) {
        instance(false).save(false);
      }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
      instance(false).sendClientInfo(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
      instance(false).sendClientInfo(event.getEntity());
    }
  }
}
