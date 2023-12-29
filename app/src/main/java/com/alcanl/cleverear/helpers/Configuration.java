package com.alcanl.cleverear.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.alcanl.cleverear.R;
import com.alcanl.cleverear.dto.BLEDeviceWrapper;
import com.alcanl.cleverear.entity.HearingAidModel;
import com.alcanl.cleverear.sdk.events.ConfigurationChangedEvent;
import com.alcanl.cleverear.sdk.events.EventManager;
import com.alcanl.cleverear.sdk.events.handlers.EventBus;
import com.ark.ArkException;
import com.ark.ProductManager;
import com.ark.WirelessCommunicationAdaptorStateType;
import java.util.concurrent.ConcurrentHashMap;
public class Configuration {

    private static final String TAG = Configuration.class.getSimpleName();
    private static final int CONNECTING = 2;
    private static final int CONNECTED = 3;
    public ProductManager productManager;
    private final ConcurrentHashMap<HearingAidModel.Side, HearingAidModel> HearingAidMap = new ConcurrentHashMap<>();
    public boolean autoConnect = true;
    private Activity activity;

    /**
     * Method to send a configuration changed event for a specific HA device specified by address
     * to all event listeners
     *
     * @param address the HA device whose configuration has changed
     */
    public void issueConfigurationChangedEvent(String address) {
        EventBus.postEvent(ConfigurationChangedEvent.class.getName(), new ConfigurationChangedEvent(address));
    }


    /**
     * Method to checks whether the descriptor is not null
     *
     * @param side hearing aid assigned side
     * @return true if the HearingAidModel for this side is in the HashMap, false if the HearingAidModel is null
     */
    public boolean isHANotNull(HearingAidModel.Side side) {
        return (getDescriptor(side) != null);
    }

    /**
     * Method to check if this hearing aid is available on this side and if it's connected
     *
     * @param side hearing aid assigned side
     * @return true if the hearing aid is available or connected, false if otherwise
     */
    public boolean isHAAvailable(HearingAidModel.Side side) {

        return (isHANotNull(side)) && getDescriptor(side).connected;
    }

    /**
     * Method to check if the current configuration is empty (no hearing aids connected)
     *
     * @return true if configuration is empty false if at least one side has a configuration present
     */
    public boolean isSavedPreferenceConfigEmpty() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String leftAddress = sharedPreferences.getString(activity.getString(R.string.left_ha_address), null);
        String rightAddress = sharedPreferences.getString(activity.getString(R.string.right_ha_address), null);

        return leftAddress == null && rightAddress == null;
    }

    /**
     * Method to check if the current configuration is empty (no hearing aids connected)
     *
     * @return true if configuration is empty false if at least one side has a configuration present
     */
    public boolean isConfigEmpty() {
        return HearingAidMap.isEmpty();
    }

    public boolean isConfigFull() {
        return (HearingAidMap.size() == 2);
    }

    /**
     * This Method saves a selected bluetooth hearing aid device that the user will use
     * addHearingAid does the following
     * - creates a new instance of HearingAidModel
     * - sets the values in the new hearing aid model instance
     * - updates the hearing aid map
     * - adds the hearing aid to the bondedHAModel (remembers)
     * - sends out a ConfigurationChangEvent to all listeners for this event.
     *
     * @param side   Whether the hearing aid is a right or left
     *               //     * @param friendlyName The nick name you want to name your bluetooth device
     * @param device The bluetooth device that will be saved as a right or left hearing aid in the HearingModel
     *               //     * @param isBonded     If the device is bonded to the phone or not
     */
    public void addHearingAid(HearingAidModel.Side side, BLEDeviceWrapper device) {
        HearingAidModel hearingAid = new HearingAidModel(side, device.getAddress());
        hearingAid.name = device.getName();
        hearingAid.manufacturerSpecificData = device.getManufacturerData();
        HearingAidMap.put(side, hearingAid);
        Context context;
        context =activity;
        save(context);
    }

    public void disconnectHA(HearingAidModel.Side side) {

        try {
            getDescriptor(side).communicationAdaptor.disconnect();
        } catch (ArkException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    public void connectHA(HearingAidModel.Side side) {
        try {
            getDescriptor(side).communicationAdaptor.connect();
        } catch (ArkException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void removeHearingAid(HearingAidModel.Side side) {

        getDescriptor(side).cleanup();

        if (getDescriptor(side).connectionStatus == CONNECTING || getDescriptor(side).connectionStatus == CONNECTED) {
            disconnectHA(side);
        }

        HearingAidMap.remove(side);
        Context context;
        context =activity;
        save(context);
    }

    /**
     * Method to return a hearing aid descriptor based on the side received in the parameter
     * * @param side the HA side we want to get the descriptor for
     *
     * @return HearingAid Model for the side asked for, otherwise return null if no hearing aid is
     * configured
     * to the side.
     */
    public HearingAidModel getDescriptor(HearingAidModel.Side side) {
        try {
            if (HearingAidMap.containsKey(side))
                return HearingAidMap.get(side);
        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }


    public void firstTimeInitialisation()
    {
        EventManager.instance().execute();
    }


    /**
     * Method to initialize the Configuration which is called when the app is first started
     *
     * @param context application wide context for access to data
     */
    public void initialiseConfiguration(Activity context) {
        setActivity(context);
        firstTimeInitialisation();
        if (!isSavedPreferenceConfigEmpty()) {
            load(context);
            if (autoConnect) {
                if (isHANotNull(HearingAidModel.Side.Left)) {
                    if (getDescriptor(HearingAidModel.Side.Left).connectionStatus != WirelessCommunicationAdaptorStateType.kConnected.ordinal())
                        connectHA(HearingAidModel.Side.Left);
                }
                if (isHANotNull(HearingAidModel.Side.Right)) {
                    if (getDescriptor(HearingAidModel.Side.Right).connectionStatus != WirelessCommunicationAdaptorStateType.kConnected.ordinal())
                        connectHA(HearingAidModel.Side.Right);
                }
            }
        }
    }


    /**
     * Method to load the shared preferences that were stored, this is done when the configuration is
     * initialized
     *
     * @param context instance of the current activity
     */
    public void load(Activity context) {
        activity = context;
        restoreSharedPreferences(context);
    }

    public void setActivity(Activity context) {
        activity = context;
    }

    /**
     * Method to save the current preferences into preference storage
     *
     * @param context instance of the current activity
     */
    public void save(Context context) {
        saveSharedPreferences(context);
    }


    private SharedPreferences ourPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private void restoreSharedPreferences(Context context) {

        SharedPreferences preferences = ourPreferences(context);
        autoConnect =  true;
        if (preferences.getString(context.getString(R.string.left_ha_address), null) != (null)) {
            restoreHA(preferences, HearingAidModel.Side.Left, context.getString(R.string.left_ha_name), context.getString(R.string.left_ha_address),
                    context.getString(R.string.left_ha_friendly_name),
                    context.getString(R.string.left_ha_manu_data), context.getString(R.string.Left_ha_currentMemory));
        }

        if (preferences.getString(context.getString(R.string.right_ha_address), null) != (null)) {
            restoreHA(preferences, HearingAidModel.Side.Right, context.getString(R.string.right_ha_name), context.getString(R.string.right_ha_address),
                    context.getString(R.string.right_ha_friendly_name),  context.getString(R.string.right_ha_manu_data), context.getString(R.string.Right_ha_currentMemory));
        }


    }



    private void restoreHA(SharedPreferences preferences, HearingAidModel.Side side, String nameTag,
                           String addressTag, String friendlyTag, String manufacturingTag, String currentMemoryTag) {

        HearingAidModel descriptor;

        if (getDescriptor(side) != null) {
            descriptor = getDescriptor(side);
        } else {
            descriptor = new HearingAidModel(side, preferences.getString(addressTag, ""));
            //   descriptor.registerReceiver();
        }

        descriptor.name = preferences.getString(nameTag, null);
        descriptor.address = preferences.getString(addressTag, null);
        descriptor.friendlyName = preferences.getString(friendlyTag, null);
        // descriptor.volume = (byte) (preferences.getInt(volumeTag, 1) & 0xFF);

        //  descriptor.bluetoothDevice = null;
        //   descriptor.currentMemory = (byte) (preferences.getInt(currentMemoryTag, 1) & 0xFF);
        descriptor.manufacturerSpecificData = preferences.getString(manufacturingTag, null);


        if (descriptor.address != null)
            HearingAidMap.put(side, descriptor);


    }

    private void saveSharedPreferences(Context context) {

        SharedPreferences preferences = ourPreferences(context);

        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(context.getString(R.string.pref_auto_conn_key), autoConnect);



        saveHA(editor, HearingAidModel.Side.Left, context.getString(R.string.left_ha_name), context.getString(R.string.left_ha_address), context.getString(R.string.left_ha_friendly_name),
                context.getString(R.string.left_ha_manu_data), context.getString(R.string.Left_ha_currentMemory));

        saveHA(editor, HearingAidModel.Side.Right, context.getString(R.string.right_ha_name), context.getString(R.string.right_ha_address), context.getString(R.string.right_ha_friendly_name),
                context.getString(R.string.right_ha_manu_data), context.getString(R.string.Right_ha_currentMemory));

        editor.apply();
    }

    private void saveHA(SharedPreferences.Editor editor, HearingAidModel.Side type, String nameTag,
                        String addressTag, String friendlyTag,  String manufacturingData, String currentMemory) {

        if (HearingAidMap.containsKey(type)) {
            HearingAidModel descriptor = HearingAidMap.get(type);
            saveHA(editor, descriptor, nameTag, addressTag, friendlyTag,manufacturingData, currentMemory);
        } else
            removeFromEditor(editor, nameTag, addressTag, friendlyTag);
    }

    private void saveHA(SharedPreferences.Editor editor, HearingAidModel descriptor, String nameTag,
                        String addressTag, String friendlyTag,  String manufacturingData, String currentMemoryTag) {

        editor.putString(nameTag, descriptor.name);
        editor.putString(addressTag, descriptor.address);
        editor.putString(friendlyTag, descriptor.friendlyName);
        editor.putString(manufacturingData, descriptor.manufacturerSpecificData);
        editor.putInt(currentMemoryTag, descriptor.currentMemory);


    }

    private void removeFromEditor(SharedPreferences.Editor editor, String nameTag, String addressTag,
                                  String friendlyTag) {
        editor.remove(nameTag);
        editor.remove(addressTag);
        editor.remove(friendlyTag);


    }


}
