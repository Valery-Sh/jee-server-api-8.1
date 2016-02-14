package org.netbeans.modules.jeeserver.base.deployment.utils.prefs;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class InstancePreferences {

    private static final Logger LOG = Logger.getLogger(InstancePreferences.class.getName());

    private final PreferencesManager manager;

    private Preferences prefs;
    
    private final String id;

    public InstancePreferences(String id, PreferencesManager manager, Preferences prefs) {
        this.manager = manager;
        this.prefs = prefs;
        this.id = id;
    }

    public PreferencesManager getManager() {
        return manager;
    }
    public String[] keys() {
        try {
            return prefs.keys();
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
            return new String[]{};
        }
    }
    public Preferences getPrefs() {
        return prefs;
    }

    public String getId() {
        return id;
    }

    public boolean getBoolean(String key, boolean def) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            return prefs.getBoolean(key, def);
        }
    }

    public double getDouble(String key, double def) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            return prefs.getDouble(key, def);
        }
    }

    public float getFloat(String key, float def) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            return prefs.getFloat(key, def);
        }
    }

    public int getInt(String key, int def) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            return prefs.getInt(key, def);
        }
    }

    public long getLong(String key, long def) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            return prefs.getLong(key, def);
        }
    }

    public String getString(String key, String def) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            return prefs.get(key, def);
        }
    }

    public void putBoolean(String key, boolean value) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            prefs.putBoolean(key, value);
        }
    }

    public void putDouble(String key, double value) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            prefs.putDouble(key, value);
        }
    }

    public void putFloat(String key, float value) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            prefs.putFloat(key, value);
        }
    }

    public void putInt(String key, int value) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            prefs.putInt(key, value);
        }
    }

    public void putLong(String key, long value) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            prefs.putLong(key, value);
        }
    }
    public void setProperty(String propName, String value) {
        this.putString(id, value);
    }
    public void getProperty(String propName) {
        this.getString(id, null);
    }

    public void putString(String key, String value) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            prefs.put(key, value);
            /*                try {
                    prefs.flush();
                } catch (Exception ex) {
                    BaseUtil.out("IManager putString EXCEPTION ex=" + ex.getMessage());
                    Exceptions.printStackTrace(ex);
                }
             */
        }
    }

    public void removeKey(String key) {
        synchronized (this) {
            if (prefs == null) {
                throw new IllegalStateException("Properties are not valid anymore");
            }
            prefs.remove(key);
        }
    }

    public void remove() {
        try {
            synchronized (this) {
                if (prefs != null) {
                    manager.remove(prefs);
                    prefs = null;
                }
            }
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
        }
    }
}
