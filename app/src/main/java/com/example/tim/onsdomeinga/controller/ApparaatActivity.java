package com.example.tim.onsdomeinga.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tim.onsdomeinga.R;
import com.example.tim.onsdomeinga.model.Device;
import com.example.tim.onsdomeinga.model.DeviceFactory;
import com.example.tim.onsdomeinga.model.DeviceTypes;
import com.example.tim.onsdomeinga.model.DigitalDevice;
import com.example.tim.onsdomeinga.model.DimmableDevice;
import com.example.tim.onsdomeinga.model.ReadableDevice;
import com.example.tim.onsdomeinga.model.SwitchableDevice;

/**
 * Activity displaying the details and settings of the devices. It is also used to create new devices.
 * Depending on the parameters this activity will hide or show certain elements accordingly.
 */

public class ApparaatActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView apparaatTitel;
    private EditText apparaatNaam;
    private EditText apparaatPoort;
    private Spinner apparaatType;
    private Switch isAan;
    private Switch isActief;
    private String geselecteerdType;
    private SeekBar niveau;
    private TextView niveauLabel;
    private TextView niveauPercentage;

    private Device mApparaat;
    private boolean isEditing;
    private boolean newDevice;
    private int deviceIndex = -1;
    private int menuToLoad = 0;
    private int dimValue = 0;

    private DeviceFactory deviceFactory = new DeviceFactory();

    /**
     * Lifecycle method starting things off. After the OS super call the contentView is connected to the Activity
     * After that executing the 4 mentioned methods to setup the activity before use.
     * @param savedInstanceState reference to Bundle object passed into the method
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apparaat);

        getIntentInfo();
        connectUI();

        setupUIElements();
        setupListeners();
    }

    /**
     * Getting the additional info (called Intent) which the 'ApparatenActivity' send along
     */
    private void getIntentInfo() {
        Intent intent = getIntent();
        String devName = intent.getStringExtra("huidigApparaat");

        // If the name which was send over is an empty string this Activity should show the layout for a new device
        // If it was not empty it should get the corresponding device from the deviceList
        if (devName.equals("")) {
            mApparaat = new ReadableDevice("", 0);
            isEditing = true;
        } else {
            // Loop through the deviceList to find the index for the device matching the devName
            for (int i = 0; i < MainActivity.deviceList.size(); i++) {
                if (MainActivity.deviceList.get(i).getName().equals(devName)) {
                    deviceIndex = i;
                    break;
                }
            }

            mApparaat = MainActivity.deviceList.get(deviceIndex);
            isEditing = intent.getBooleanExtra("willEdit", false);
        }
    }

    // Method to show the keyboard once the Activity shows and the first EditText view is active
    private void showKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    /**
     * This methods finds all layout elements and does some basic layout setup
     */
    private void connectUI() {
        // Connecting the different top-menus
        int menuEditRemove = R.menu.menu_edit_remove;
        int menuSave = R.menu.menu_save;
        int menuCancelSave = R.menu.menu_cancel_save;

        // Determine if the Activity should show the layout for a new device, editing a device or just viewing a device
        if (isEditing && mApparaat.getName().equals("")) {
            setTitle("Nieuw apparaat");
            showKeyboard();
            newDevice = true;
            menuToLoad = menuSave;
        } else if (isEditing) {
            setTitle("Apparaat wijzigen");
            showKeyboard();
            newDevice = false;
            menuToLoad = menuCancelSave;
        } else {
            setTitle("Apparaat");
            newDevice = false;
            menuToLoad = menuEditRemove;
        }

        // Connecting all attributes to the elements in the layout-file
        apparaatTitel = findViewById(R.id.apparaatTitel);
        apparaatNaam = findViewById(R.id.edit_apparaatNaam);
        apparaatPoort = findViewById(R.id.edit_poort);
        apparaatType = findViewById(R.id.spinner_device_type);
        isAan = findViewById(R.id.switch_isAan);
        isActief = findViewById(R.id.switch_isActief);
        niveau = findViewById(R.id.seekbar_level);
        niveauLabel = findViewById(R.id.seekbarLabel);
        niveauPercentage = findViewById(R.id.seekbarPercentage);
    }

    /**
     * After connecting all attributes with the layout elements the attributes have to be configured.
     * This method also constructs the dropdown list with the several device type options.
     */
    private void setupUIElements() {
        // Enabling or disabling field and buttons depending on which kind of Activity needs to be shown
        apparaatNaam.setEnabled(isEditing);
        apparaatPoort.setEnabled(isEditing);
        apparaatType.setEnabled(newDevice);
        isAan.setEnabled(mApparaat.isActivated());
        isActief.setEnabled(isEditing);

        // Setting up the fields and buttons with the correct data of the device we're working with
        apparaatTitel.setText(mApparaat.getName());
        apparaatNaam.setText(mApparaat.getName());
        String portName = newDevice ? "" : ("" + mApparaat.getPort());
        apparaatPoort.setText(portName);
        if (mApparaat instanceof DigitalDevice) {
            isAan.setChecked(mApparaat.getSwitchedOn());
        }
        isActief.setChecked(mApparaat.isActivated());

        // Creating the dropdown list with the device type options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.device_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        apparaatType.setAdapter(adapter);
        apparaatType.setOnItemSelectedListener(this);

        // Depending on which type of device we're working with selecting the corresponding item in the list
        if (mApparaat instanceof ReadableDevice) {
            apparaatType.setSelection(0);
        } else if (mApparaat instanceof SwitchableDevice){
            apparaatType.setSelection(1);
        } else if (mApparaat instanceof DimmableDevice) {
            apparaatType.setSelection(2);
        }
    }

    /**
     * This methods sets up all the action- and onClicklisteners for the elements that need those
     */
    private void setupListeners() {
        // Connecting the listener to the 'apparaatNaam' EditField, once it changes the 'apparaatTitel' should be changed directly as well (onTextChanged)
        apparaatNaam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Required method, not used in this app
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    apparaatTitel.setText(s);
                } else {
                    apparaatTitel.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Required method, not used in this app
            }
        });

        // Connecting the onClickListener to the 'isAan' switch, directly switching on or off the device if the Activity is in viewing mode
        // While the Activity is in editing or new device mode the change is not directly handled, that will be done when saving the device
        isAan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditing || !newDevice) {
                    mApparaat.setSwitchedOn(isAan.isChecked());
                }
            }
        });

        // Connecting the onClickListener for the 'isActief' switch. Directly dis- or enabling the 'isAan' switch and 'niveau' slider in the app
        isActief.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApparaat.setActivated(isActief.isChecked());

                isAan.setEnabled(isActief.isChecked());
                niveau.setEnabled(isActief.isChecked());
            }
        });

        // Connecting the onSeekBarChangeListener to the 'niveau' slider, if the device is an instance of DimmableDevice the slider will directly forward it's value to the device and adjust the written percentage
        niveau.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                niveauPercentage.setText("" + progress + "%");
                dimValue = progress;

                if (mApparaat instanceof DimmableDevice) {
                    ((DimmableDevice) mApparaat).setDimValue(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Required method, not used in this app
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Required method, not used in this app
            }
        });
    }

    // Method to hide the keyboard when called
    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException npe) {
            System.out.println("Fontys - Error hiding keyboard.");
        }
    }

    /**
     * Method to check the settings in the Activity and either create a new device of update an existing one.
     */
    private void saveDevice() {
        // Collecting the selected device type and name of the device
        String type = geselecteerdType;
        String name = apparaatNaam.getText().toString();
        int port;

        // Check if the device name has an acceptable length, if not the method stops here and a Toast-message will be shown
        if (!mApparaat.checkStringLength(name)) {
            Toast.makeText(this, "Please enter a valid name.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Try to convert the entered port-number from an Integer to String and validate it. If it's not valid the method stops here and a Toast-message will be shown
        try {
            port = Integer.parseInt(apparaatPoort.getText().toString());

            if (!mApparaat.validatePort(port)) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException nfe) {
            Toast.makeText(this, "Please enter a valid portnumber.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Checking if we're working with a new device or an existing one
        if (newDevice) {
            // If we're here it means the device is new, first check if it has a valid name. If not the methods stops here and a Toast-message is shown
            if (!mApparaat.changeName(name)) {
                Toast.makeText(this, "Please enter a valid name.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Creating the new device in the deviceFactory and add it to the deviceList
            MainActivity.deviceList.add(deviceFactory.getDevice(type, name, port, false));

            // Request the device again
            Device tempDev = MainActivity.deviceList.get(MainActivity.deviceList.size() - 1);

            // Adjust the settings of the device
            adjustDevice(tempDev);

            // Show a toast that the device was created and added
            Toast.makeText(this, "Apparaat \'" + name + "\' was added.", Toast.LENGTH_SHORT).show();
        } else if (isEditing) {
            // If we're here it means the device already existed and is being saved after possible changes
            // Request the device from the list
            Device tempDev = MainActivity.deviceList.get(deviceIndex);

            // Check if the name is still valid
            if (!tempDev.changeName(name)) {
                Toast.makeText(this, "Please enter a valid name.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Change the port to the new value (whether or not it was changed)
            tempDev.changePort(port);

            // Adjust the settings of the device
            adjustDevice(tempDev);

            // Show a toast that the device was updated
            Toast.makeText(this, "Updated apparaat \'" + name + "\'.", Toast.LENGTH_SHORT).show();
        }

        // After saving the device finish the Activity and go back to the previous Activity
        this.finish();
    }

    // For both a new device and an existing one adjusting the values of some attributes
    private void adjustDevice(Device tempDev) {
        if (tempDev instanceof DigitalDevice) {
            tempDev.setSwitchedOn(isAan.isChecked());
        }

        tempDev.setActivated(isActief.isChecked());

        if (tempDev instanceof DimmableDevice) {
            ((DimmableDevice) tempDev).setDimValue(dimValue);
        }
    }

    /**
     * The methods builds an AlertDialog in order to show an alert message and two buttons.
     * The user will have to confirm he really wants to remove the device he has on screen or he
     * can choose to reconsider and not remove it.
     */
    private void removeDeviceAlert() {
        // Create the alert builder
        AlertDialog.Builder alert = new AlertDialog.Builder(ApparaatActivity.this);
        // Adding a title and a message
        alert.setTitle("Apparaat verwijderen?");
        alert.setMessage("Weet u zeker dat u dit apparaat wilt verwijderen?");
        // Adding the two buttons, positive to confirm, negative to reconsider
        alert.setPositiveButton("Ja, verwijder!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // If the user confirms to remove the device then the removeDevice-method will be called
                removeDevice();
            }
        });
        alert.setNegativeButton("Nee, toch niet.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // If the user reconsidered and doesn't want to remove the device a Toast-message will be shown that the action was cancelled
                Toast.makeText(ApparaatActivity.this, "Deleting cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        // The user has to choose between the given options, can't press beside the alert to dismiss it
        alert.setCancelable(false);
        // Setting an icon in the alert
        alert.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_delete_black));

        alert.create().show();
    }

    /**
     * When the user confirmed to remove the device in the removeDeviceAlert, this method will actually remove it from the deviceList
     */
    private void removeDevice() {
        String name = apparaatNaam.getText().toString();

        MainActivity.deviceList.remove(mApparaat);
        Toast.makeText(this, "Removed apparaat \'" + name + "\'.", Toast.LENGTH_SHORT).show();

        System.out.println("Fontys - Size DeviceList = " + MainActivity.deviceList.size());
        this.finish();
    }

    /**
     * Method to show or hide the layout elements concerning the slider and percentage
     * @param visibility either View.GONE or View.VISIBLE will be send to hide or show respectively
     */
    private void setVisibilityForNiveau(int visibility) {
        niveau.setVisibility(visibility);
        niveauLabel.setVisibility(visibility);
        niveauPercentage.setVisibility(visibility);
    }

    /**
     * Methods to enable or disable the niveau slider depending on the device being active or not and a parameter
     * @param enable a boolean determining whether to disable or enable the slider
     */
    private void setEnabledForNiveau(boolean enable) {
        if (!mApparaat.isActivated() || !enable) {
            niveau.setEnabled(false);
        } else {
            niveau.setEnabled(true);
        }
    }

    /**
     * Method belong to device type dropdown list. Depending on the selected option various things happen
     * @param parent the AdapterView (list with options) where the selection happened
     * @param view the option (view) which was clicked
     * @param position the position of the view in the adapter
     * @param id the row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Get the String version of the selected type
        geselecteerdType = parent.getItemAtPosition(position).toString();

        // Taking some actions depending on which type was selected
        if (geselecteerdType.equals(DeviceTypes.READONLY.getDescription())) {
            // Readonly was selected so we should NOT see the 'isAan' switch but DO want to see the slider and percentage part
            isAan.setVisibility(View.GONE);
            setVisibilityForNiveau(View.VISIBLE);

            // Get the current dimvalue of the device, set the value on the slider and text, disable the slider itself
            dimValue = mApparaat.getValue();
            niveau.setProgress(dimValue);
            niveauPercentage.setText(Integer.toString(dimValue));
            setEnabledForNiveau(false);
        } else if (geselecteerdType.equals(DeviceTypes.SWITCHABLE.getDescription())) {
            // Switchable was selected so we should see the 'isAan' switch but NOT the slider and percentage part. Disable the slider
            isAan.setVisibility(View.VISIBLE);
            setVisibilityForNiveau(View.GONE);
            setEnabledForNiveau(false);
        } else if (geselecteerdType.equals(DeviceTypes.DIMMABLE.getDescription())) {
            // Dimmable was selected so we should see both the 'isAan' switch and the slider and percentage part
            isAan.setVisibility(View.VISIBLE);
            setVisibilityForNiveau(View.VISIBLE);

            // If it's not a new device, get the current dimvalue and set it on the slider and text. Enabling the slider itself as well
            if (!newDevice) {
                dimValue = ((DimmableDevice) mApparaat).getDimValue();
                niveau.setProgress(dimValue);
                niveauPercentage.setText(dimValue + "%");
                setEnabledForNiveau(true);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Required method, not used here
    }

    /**
     * Method to reload the activity after a certain event, reload either in edit mode or view mode
     * @param willEdit determines whether the activity will be reload in edit mode or not
     */
    private void reloadActivity(boolean willEdit) {
        Intent intent = getIntent();
        intent.putExtra("willEdit", willEdit);
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    /**
     * Method to add the correct menu options in the top bar in the activity
     * @param menu the menu to be loaded
     * @return if there is a menu to load return true otherwise false
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menuToLoad != 0) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(menuToLoad, menu);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Methods to handle the various buttons in the menu and the presses on the them
     * @param item the button on which the user pressed
     * @return If a custom item/button was pressed return true otherwise the default super call will be made
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // If the keyboard is open then hide it
        hideKeyboard();

        // Get the id of the button which was pressed
        int id = item.getItemId();

        // Compare the pressed button to the options we have
        switch (id) {
            case android.R.id.home:
                // The back arrow was pressed, just finish the activity and go back to the previous
                this.finish();
                return true;
            case R.id.cancelChange:
                // The cancel change button was pressed, reload the activity to show the show mode
                reloadActivity(false);
                return true;
            case R.id.edit:
                // The edit button was pressed, reload the activity to show the edit mode
                reloadActivity(true);
                return true;
            case R.id.remove:
                // The remove button was pressed, show the removeDeviceAlert before removing it
                removeDeviceAlert();
                return true;
            case R.id.save:
                // The save button (visible on new devices) was pressed, call the saveDevice method
                saveDevice();
                return true;
            case R.id.saveChange:
                // The save change button (visible on editing devices) was pressed, call the saveDevice method
                saveDevice();
                return true;
            default:
                // None of the above options was selected, execute the default super call
                return super.onOptionsItemSelected(item);
        }
    }
}
