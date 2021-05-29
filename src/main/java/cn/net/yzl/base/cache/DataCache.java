package cn.net.yzl.base.cache;

import android.content.Context;
import android.text.TextUtils;
import android.util.LruCache;

public class DataCache {
    private MemoryLrucache memoryCache;
    /**内存最大存储*/
    private final static int MAX_SIZE_MEMORY = (int)Runtime.getRuntime().maxMemory()/10;
    static DataCache instance;
    private Context context;
    private DataCache() {
    }

    public static DataCache getInstance(Context context) {
        if (instance == null) {
            synchronized (DataCache.class) {
                instance = new DataCache();
                instance.context = context;
            }
        }
        return instance;
    }

    /**
     * 数据存储到内存中
     *
     * @param data
     */
    public void storeToMemory(String key, String data) {
        if (memoryCache == null) {
            memoryCache = new MemoryLrucache();
        }
        memoryCache.put(key , data);
    }

    public String getFromMemory(String key) {
        if (memoryCache == null) {
            return null;
        }
        String data = memoryCache.get(key);
        return data;
    }

    public boolean exitInMemory(String key) {
        if (memoryCache == null) {
            return false;
        }
        String data = memoryCache.get(key);
        boolean exit = !TextUtils.isEmpty(data);
        return exit;
    }

    class MemoryLrucache extends LruCache<String, String> {


        public MemoryLrucache() {
            super(MAX_SIZE_MEMORY);
        }

        @Override
        protected int sizeOf(String key, String value) {
            return value.getBytes().length;
        }
    }
}
