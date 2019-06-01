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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apparaat);

        getIntentInfo();
        connectUI();

        setupUIElements();
        setupListeners();
    }

    private void getIntentInfo() {
        Intent intent = getIntent();
        String devName = intent.getStringExtra("huidigApparaat");

        if (devName.equals("")) {
            mApparaat = new ReadableDevice("", 0);
            isEditing = true;
        } else {
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

    private void showKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void connectUI() {
        int menuEditRemove = R.menu.menu_edit_remove;
        int menuSave = R.menu.menu_save;
        int menuCancelSave = R.menu.menu_cancel_save;

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

    private void setupUIElements() {
        apparaatNaam.setEnabled(isEditing);
        apparaatPoort.setEnabled(isEditing);
        apparaatType.setEnabled(newDevice);
        isAan.setEnabled(mApparaat.isActivated());
        isActief.setEnabled(isEditing);

        apparaatTitel.setText(mApparaat.getName());
        apparaatNaam.setText(mApparaat.getName());
        String portName = newDevice ? "" : ("" + mApparaat.getPort());
        apparaatPoort.setText(portName);
        if (mApparaat instanceof DigitalDevice) {
            isAan.setChecked(mApparaat.getSwitchedOn());
        }
        isActief.setChecked(mApparaat.isActivated());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.device_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        apparaatType.setAdapter(adapter);
        apparaatType.setOnItemSelectedListener(this);

        if (mApparaat instanceof ReadableDevice) {
            apparaatType.setSelection(0);
        } else if (mApparaat instanceof SwitchableDevice){
            apparaatType.setSelection(1);
        } else if (mApparaat instanceof DimmableDevice) {
            apparaatType.setSelection(2);
        }
    }

    private void setupListeners() {
        apparaatNaam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
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
            }
        });

        isAan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditing || !newDevice) {
                    mApparaat.setSwitchedOn(isAan.isChecked());
                }
            }
        });

        isActief.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApparaat.setActivated(isActief.isChecked());

                isAan.setEnabled(isActief.isChecked());
                niveau.setEnabled(isActief.isChecked());
            }
        });
        
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
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException npe) {
            System.out.println("Fontys - Error hiding keyboard.");
        }
    }

    private void saveDevice() {
        String type = geselecteerdType;
        String name = apparaatNaam.getText().toString();
        int port;

        if (!mApparaat.checkStringLength(name)) {
            Toast.makeText(this, "Please enter a valid name.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            port = Integer.parseInt(apparaatPoort.getText().toString());

            if (!mApparaat.validatePort(port)) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException nfe) {
            Toast.makeText(this, "Please enter a valid portnumber.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newDevice) {
            if (!mApparaat.changeName(name)) {
                Toast.makeText(this, "Please enter a valid name.", Toast.LENGTH_SHORT).show();
                return;
            }

            MainActivity.deviceList.add(deviceFactory.getDevice(type, name, port, false));

            Device tempDev = MainActivity.deviceList.get(MainActivity.deviceList.size() - 1);

            if (tempDev instanceof DigitalDevice) {
                tempDev.setSwitchedOn(isAan.isChecked());
            }

            tempDev.setActivated(isActief.isChecked());

            if (tempDev instanceof DimmableDevice) {
                ((DimmableDevice) tempDev).setDimValue(dimValue);
            }

            Toast.makeText(this, "Apparaat \'" + name + "\' was added.", Toast.LENGTH_SHORT).show();
        } else if (isEditing) {
            Device tempDev = MainActivity.deviceList.get(deviceIndex);

            if (!tempDev.changeName(name)) {
                Toast.makeText(this, "Please enter a valid name.", Toast.LENGTH_SHORT).show();
                return;
            }

            tempDev.changePort(port);

            if (tempDev instanceof DigitalDevice) {
                tempDev.setSwitchedOn(isAan.isChecked());
            }

            tempDev.setActivated(isActief.isChecked());

            if (tempDev instanceof DimmableDevice) {
                ((DimmableDevice) tempDev).setDimValue(dimValue);
            }

            Toast.makeText(this, "Updated apparaat \'" + name + "\'.", Toast.LENGTH_SHORT).show();
        }

        this.finish();
    }

    private void removeDeviceAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(ApparaatActivity.this);
        alert.setTitle("Apparaat verwijderen?");
        alert.setMessage("Weet u zeker dat u dit apparaat wilt verwijderen?");
        alert.setPositiveButton("Ja, verwijder!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeDevice();
            }
        });
        alert.setNegativeButton("Nee, toch niet.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ApparaatActivity.this, "Deleting cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        alert.setCancelable(false);
        alert.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_delete_black));

        alert.create().show();
    }

    private void removeDevice() {
        String name = apparaatNaam.getText().toString();

        MainActivity.deviceList.remove(mApparaat);
        Toast.makeText(this, "Removed apparaat \'" + name + "\'.", Toast.LENGTH_SHORT).show();

        System.out.println("Fontys - Size DeviceList = " + MainActivity.deviceList.size());
        this.finish();
    }

    private void setVisibilityForNiveau(int visibility) {
        niveau.setVisibility(visibility);
        niveauLabel.setVisibility(visibility);
        niveauPercentage.setVisibility(visibility);
    }

    private void setEnabledForNiveau(boolean enable) {
        if (!mApparaat.isActivated() || !enable) {
            niveau.setEnabled(false);
        } else {
            niveau.setEnabled(true);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        geselecteerdType = parent.getItemAtPosition(position).toString();

        if (geselecteerdType.equals(DeviceTypes.READONLY.getDescription())) {
            isAan.setVisibility(View.GONE);
            setVisibilityForNiveau(View.VISIBLE);

            dimValue = mApparaat.getValue();
            niveau.setProgress(dimValue);
            niveauPercentage.setText(Integer.toString(dimValue));
            setEnabledForNiveau(false);
        } else if (geselecteerdType.equals(DeviceTypes.SWITCHABLE.getDescription())) {
            isAan.setVisibility(View.VISIBLE);
            setVisibilityForNiveau(View.GONE);
            setEnabledForNiveau(false);
        } else if (geselecteerdType.equals(DeviceTypes.DIMMABLE.getDescription())) {
            isAan.setVisibility(View.VISIBLE);
            setVisibilityForNiveau(View.VISIBLE);

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
    }

    private void reloadActivity(boolean willEdit) {
        Intent intent = getIntent();
        intent.putExtra("willEdit", willEdit);
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideKeyboard();

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.cancelChange:
                reloadActivity(false);
                return true;
            case R.id.edit:
                reloadActivity(true);
                return true;
            case R.id.remove:
                removeDeviceAlert();
                return true;
            case R.id.save:
                saveDevice();
                return true;
            case R.id.saveChange:
                saveDevice();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
