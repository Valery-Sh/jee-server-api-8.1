package org.netbeans.modules.jeeserver.base.deployment.utils.prefs;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

public class InstancePreferences implements PreferencesProperties {

    private static final Logger LOG = Logger.getLogger(InstancePreferences.class.getName());

    private final Preferences prefs;

    private final String id;

    public InstancePreferences(String id, Preferences prefs) {
        this.prefs = prefs;
        this.id = id;
    }

    @Override
    public String[] keys() {
        try {
            return prefs.keys();
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
            return new String[]{};
        }
    }

    @Override
    public Preferences getPreferences() {
        return prefs;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            return prefs.getBoolean(key, def);
        }
    }

    @Override
    public double getDouble(String key, double def) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            return prefs.getDouble(key, def);
        }
    }

    @Override
    public float getFloat(String key, float def) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            return prefs.getFloat(key, def);
        }
    }

    @Override
    public int getInt(String key, int def) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            return prefs.getInt(key, def);
        }
    }

    @Override
    public long getLong(String key, long def) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            return prefs.getLong(key, def);
        }
    }

    @Override
    public String getString(String key, String def) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            return prefs.get(key, def);
        }
    }

    @Override
    public void putBoolean(String key, boolean value) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            prefs.putBoolean(key, value);
        }
    }

    @Override
    public void putDouble(String key, double value) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            prefs.putDouble(key, value);
        }
    }

    @Override
    public void putFloat(String key, float value) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            prefs.putFloat(key, value);
        }
    }

    @Override
    public void putInt(String key, int value) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            prefs.putInt(key, value);
        }
    }

    @Override
    public void putLong(String key, long value) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            prefs.putLong(key, value);
        }
    }

    @Override
    public InstancePreferences setProperty(String propName, String value) {
        this.putString(propName, value);
        return this;
    }

    @Override
    public String getProperty(String propName) {
        return this.getString(propName, null);
    }

    @Override
    public void putString(String key, String value) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            prefs.put(key, value);
        }
    }

    @Override
    public void removeKey(String key) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            prefs.remove(key);
        }
    }

    @Override
    public boolean remove() {

        boolean success = false;

        try {
            synchronized (this) {
                if (prefs != null) {
                    prefs.removeNode();
                }
                success = !prefs.nodeExists("");
            }
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
        }
        return success;
    }

    //
    //
    //
    @Override
    public Map<String, String> toMap() {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            Map<String, String> map = new HashMap<>();
            for (String key : keys()) {
                map.put(key, prefs.get(key, null));
            }
            return map;
        }
    }
    @Override
    public Properties toProperties() {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            Properties props = new Properties();
            for (String key : keys()) {
                props.put(key, prefs.get(key, null));
            }
            return props;
        }
    }

    @Override
    public PreferencesProperties copyFrom(Properties props) {
        if (props == null || props.isEmpty()) {
            return this;
        }

        Enumeration en = props.propertyNames();
        while (en.hasMoreElements()) {
            String nm = (String) en.nextElement();
            putString(nm, props.getProperty(nm));
        }
        return this;
    }

    @Override
    public PreferencesProperties copyFrom(Map<String,String> props) {
        if (props == null || props.isEmpty()) {
            return this;
        }
        props.forEach( (k,v) -> {
            putString(k,v);
        });
        
        return this;
    }
    
    @Override
    public void clear() {
        try {
            synchronized (this) {
                if (prefs != null) {
                    for (String key : prefs.keys()) {
                        prefs.remove(key);
                    }
                }
            }
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
        }

    }

    @Override
    public void removeKeys(Predicate<String> predicate) {
        for (String key : keys()) {
            if (predicate.test(key)) {
                removeKey(key);
            }
        }
    }

    @Override
    public void forEach(BiConsumer<String, String> action) {
        String[] keys = keys();
        for (String key : keys) {
            action.accept(key, getString(key, null));
        }

    }
    @Override
    public Map<String, String> filter(BiPredicate<String, String> predicate) {
        Map<String, String> map = new HashMap<>();
        String[] keys = keys();
        for (String key : keys) {
            String value = getString(key, null);
            if (predicate.test(key, value)) {
                map.put(key, value);
            }
        }
        return map;
    }
    @Override
    public Stream<String> keyStream() {
        List<String> list = Arrays.asList(keys());
        return list.stream();
    }

    @Override
    public int size() {
        return keys().length;
    }
}
