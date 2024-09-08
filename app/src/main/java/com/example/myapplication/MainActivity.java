package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private FlatAdapter flatAdapter;
    private DatabaseHelper dbHelper;
    private Button button1, button2, button3, button4;
    private Button currentSaveButton;
    private Button printButton;
    private int currentListType = 1;
    private static final int REQUEST_WRITE_PERMISSION = 786;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    public static boolean isLock = false;
    private String[] months = {"January","February","March","April","May","June", "July", "August","September", "October", "November", "December" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        // Initialize ListView and Buttons
        listView = findViewById(R.id.listView);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        currentSaveButton = findViewById(R.id.saveButton);

        // Set up button click listeners
        button1.setOnClickListener(view -> updateListView(1, button1));
        button2.setOnClickListener(view -> updateListView(2, button2));
        button3.setOnClickListener(view -> updateListView(3, button3));
        button4.setOnClickListener(view -> updateListView(4, button4));

        printButton = findViewById(R.id.printButton);
        printButton.setOnClickListener(view -> {
            if (checkPermission()) {
                boolean isSavedSuccessfully = saveAllReadings();
                if (isSavedSuccessfully) {
                    exportFlatsToPdf();
                }
            } else {
                requestPermission();
            }
        });

        // Set up save button click listener
        currentSaveButton.setOnClickListener(view -> {
            boolean isSaved = saveAllReadings();
            if (isSaved) {
                Toast.makeText(getApplicationContext(), "Readings saved", Toast.LENGTH_SHORT).show();
            }
        });

        // Load the default list (e.g., list 1)
        updateListView(1, button1);

        // Create notification channel
        createNotificationChannel();
        // Check and request notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            showSetValuesDialog();
            return true;
        }

        if (id == R.id.action_update) {
            if(isLock) {
                updateprevreadingfunctonality();
            }
            return true;
        }

        if (id == R.id.action_lock) {
            // Check if the current state is locked and the user is trying to unlock it
            if (!isLock) {
                // Show confirmation dialog before unlocking
                new AlertDialog.Builder(this)
                        .setTitle("Confirm Unlock")
                        .setMessage("Are you sure you want to unlock?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // User confirmed to unlock, toggle the isLock variable
                            isLock = true;

                            // Change the icon background color based on isLocked state
                            item.getIcon().setTint(getResources().getColor(R.color.button_secondary_color)); // Red color when unlocked

                            // Broadcast the lock state change
                            Intent intent = new Intent("LOCK_STATE_CHANGED");
                            intent.putExtra("isLock", isLock);
                            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show();
            } else {

                isLock = false;

                // Change the icon background color based on isLocked state
                item.getIcon().setTint(getResources().getColor(R.color.lock_icon_unlocked_color)); // Green color when locked

                // Broadcast the lock state change
                Intent intent = new Intent("LOCK_STATE_CHANGED");
                intent.putExtra("isLock", isLock);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public static boolean getIsLock() {
        return isLock;
    }
    private void updateprevreadingfunctonality() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_update, null);

        // Create the outer dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Show the outer dialog
        AlertDialog outerDialog = builder.create();
        outerDialog.show();

        // Find and set up the Update button
        Button updateButton = dialogView.findViewById(R.id.dialog_update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Confirm Update")
                    .setMessage("Are you sure you want to update?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle the update action
                            if (saveAllReadingsForAllFlats()) {
                                dbHelper.performUpdate();
                                int newindex_month = ((PreferenceUtils.getIndex_month(MainActivity.this)) +1) % 13;
                                PreferenceUtils.updateIndexmonth(MainActivity.this, newindex_month);
                                updateListView(currentListType, getSelectedButton());

                            }
                            // Close the outer dialog as well
                            outerDialog.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Close the outer dialog when "No" is clicked
                            outerDialog.dismiss();
                        }
                    }).show();
            }
        });
    }


    private void showSetValuesDialog() {
        // Get current values from SharedPreferences
        int multiplier = PreferenceUtils.getMultiplier(this);
        int fixedMaintenance = PreferenceUtils.getFixedMaintenance(this);

        // Create the AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Values");

        // Create a LinearLayout to hold the labels and EditTexts
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10); // Optional: Set padding for better layout

        // Create TextView for multiplier label
        TextView multiplierLabel = new TextView(this);
        multiplierLabel.setText("Water rate (Rs/KL):");
        multiplierLabel.setTextSize(16);
        multiplierLabel.setPadding(0, 10, 0, 5);
        layout.addView(multiplierLabel);

        // Create EditText for multiplier
        final EditText multiplierInput = new EditText(this);
        multiplierInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        multiplierInput.setText(String.valueOf(multiplier));
        multiplierInput.setHint("Enter water rate");
        multiplierInput.setEnabled(isLock);
        layout.addView(multiplierInput);

        // Create TextView for fixed maintenance label
        TextView fixedMaintenanceLabel = new TextView(this);
        fixedMaintenanceLabel.setText("Fixed Maintenance:");
        fixedMaintenanceLabel.setTextSize(16);
        fixedMaintenanceLabel.setPadding(0, 20, 0, 5);
        layout.addView(fixedMaintenanceLabel);

        // Create EditText for fixed maintenance
        final EditText fixedMaintenanceInput = new EditText(this);
        fixedMaintenanceInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        fixedMaintenanceInput.setText(String.valueOf(fixedMaintenance));
        fixedMaintenanceInput.setHint("Enter fixed maintenance");
        fixedMaintenanceInput.setEnabled(isLock);
        layout.addView(fixedMaintenanceInput);

        // Set the layout in the dialog
        builder.setView(layout);

        // Set the positive button to save values
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    // Get and save the multiplier
                    int newMultiplier = Integer.parseInt(multiplierInput.getText().toString());
                    PreferenceUtils.updateMultiplier(MainActivity.this, newMultiplier);

                    // Get and save the fixed maintenance
                    int newFixedMaintenance = Integer.parseInt(fixedMaintenanceInput.getText().toString());
                    PreferenceUtils.updateFixedMaintenance(MainActivity.this, newFixedMaintenance);

                    Toast.makeText(MainActivity.this, "Values updated successfully", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Invalid number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set the negative button to cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Show the dialog
        builder.show();
    }


    private void updateListView(int listType, Button selectedButton) {
        List<Flat> flats = dbHelper.getFlatsByListType(listType);

        if (flats.isEmpty()) {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }

        // Update the ListView adapter
        flatAdapter = new FlatAdapter(this, flats);
        listView.setAdapter(flatAdapter);

        // Change the color of the selected button
        resetButtonColors();
        if (selectedButton != null) {
            selectedButton.setBackgroundColor(getResources().getColor(R.color.wing_button_selected_color));
        }
        currentListType = listType; // Update the current list type
    }

    private void resetButtonColors() {
        button1.setBackgroundColor(getResources().getColor(R.color.wing_button_default_color));
        button2.setBackgroundColor(getResources().getColor(R.color.wing_button_default_color));
        button3.setBackgroundColor(getResources().getColor(R.color.wing_button_default_color));
        button4.setBackgroundColor(getResources().getColor(R.color.wing_button_default_color));
    }

    private boolean saveAllReadings() {
        if(!isLock){
            Toast.makeText(this, "Fields Locked!", Toast.LENGTH_SHORT).show();
            return false;
        }
        List<Flat> flats = flatAdapter.getFlats(); // Get all flats from adapter
        boolean allValid = true; // Flag to track if all readings are valid

        // First, validate all readings
        for (Flat flat : flats) {
            try {
                int previousReading = Integer.parseInt(flat.getPreviousReading());
                int currentReading = Integer.parseInt(flat.getCurrentReading());

                if (currentReading <= previousReading) {
                    allValid = false; // Set flag to false if any reading is invalid
                    break; // Exit loop early since validation failed
                }
            } catch (NumberFormatException e) {
                allValid = false; // Set flag to false if any number format exception occurs
                break; // Exit loop early
            }
        }

        if (allValid) {
            // If all readings are valid, update the database and show the success message
            for (Flat flat : flats) {
                String currentReading = flat.getCurrentReading();
                String totalMaintenance = flat.getTotalMaintenance();
                dbHelper.updateFlat(flat.getId(), currentReading, totalMaintenance); // Update database
            }

            updateListView(currentListType, getSelectedButton()); // Refresh the ListView to reflect updated data
            return true;
        } else {
            // If any reading was invalid, show the error message
            Toast.makeText(this, "All Readings should be greater than or equal to previous reading", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean saveAllReadingsForAllFlats() {
        List<Flat> flats = dbHelper.getAllFlats(); // Get all flats from the database
        boolean allValid = true; // Flag to track if all readings are valid

        // First, validate all readings
        for (Flat flat : flats) {
            try {
                int previousReading = Integer.parseInt(flat.getPreviousReading());
                int currentReading = Integer.parseInt(flat.getCurrentReading());

                if (currentReading <= previousReading) {
                    allValid = false; // Set flag to false if any reading is invalid
                    break; // Exit loop early since validation failed
                }
            } catch (NumberFormatException e) {
                allValid = false; // Set flag to false if any number format exception occurs
                break; // Exit loop early
            }
        }

        if (allValid) {
            // If all readings are valid, update the database and show the success message
            for (Flat flat : flats) {
                String currentReading = flat.getCurrentReading();
                String totalMaintenance = flat.getTotalMaintenance();
                dbHelper.updateFlat(flat.getId(), currentReading, totalMaintenance); // Update database
            }

            Toast.makeText(this, "Readings Updated Successfully for all Flats", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            // If any reading was invalid, show the error message
            Toast.makeText(this, "All Readings should be greater than previous reading", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    private Button getSelectedButton() {
        switch (currentListType) {
            case 1:
                return button1;
            case 2:
                return button2;
            case 3:
                return button3;
            case 4:
                return button4;
            default:
                return null;
        }
    }

    private String getListName() {
        switch (currentListType) {
            case 1:
                return "Siddh-A";
            case 2:
                return "Siddh-B";
            case 3:
                return "Sargam-A";
            case 4:
                return "Sargam-B";
            default:
                return "unknown";
        }
    }

    public void exportFlatsToPdf() {

        int index_month = PreferenceUtils.getIndex_month(this);
        String fileName = getListName() + "_" + months[index_month] + "_records.pdf";

        try {
            PdfWriter writer;
            PdfDocument pdfDocument;
            Document document;
            Uri pdfUri = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                pdfUri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

                if (pdfUri != null) {
                    writer = new PdfWriter(getContentResolver().openOutputStream(pdfUri));
                    pdfDocument = new PdfDocument(writer);
                    document = new Document(pdfDocument);

                    // Add table to document
                    document.add(createTable());

                    document.close();
                    showPdfSavedDialog(fileName, pdfUri);
                    showNotification(fileName, pdfUri);
                }
            } else {
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File filePath = new File(downloadsDir, fileName);
                pdfUri = Uri.fromFile(filePath);

                writer = new PdfWriter(new FileOutputStream(filePath));
                pdfDocument = new PdfDocument(writer);
                document = new Document(pdfDocument);

                // Add table to document
                document.add(createTable());

                document.close();
                showPdfSavedDialog(fileName, pdfUri);
                showNotification(fileName, pdfUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Error saving PDF: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void showPdfSavedDialog(String fileName, Uri pdfUri) {
        new AlertDialog.Builder(this)
                .setTitle("PDF Saved")
                .setMessage("PDF saved successfully as " + fileName + " in Downloads")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }


    private Table createTable() {
        List<Flat> flats = flatAdapter.getFlats();
        int multiplier = PreferenceUtils.getMultiplier(this);
        int fixMaintenance = PreferenceUtils.getFixedMaintenance(this);
        int index_month = PreferenceUtils.getIndex_month(this);
        String updatemonth = months[index_month];

        // Create a table with a single cell for the heading
        Table headingTable = new Table(1);
        headingTable.addCell(new Cell().add(new Paragraph(getListName() + " " + updatemonth + " Report").setFontSize(25).setBold()).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));

        // Add the date below the heading
        String currentDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date());
        headingTable.addCell(new Cell().add(new Paragraph("Date: " +currentDate).setFontSize(17).setBold()).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));

        // Create the main table with 8 columns
        Table table = new Table(8);

        // Add headers to the table
        table.addHeaderCell("Flat No.");
        table.addHeaderCell("Prev. Reading");
        table.addHeaderCell("Curr. Reading");
        table.addHeaderCell("Total Units");
        table.addHeaderCell("Rate/KL");
        table.addHeaderCell("Water Bill");
        table.addHeaderCell("Fixed Maint.");
        table.addHeaderCell("Total Maint.");

        // Add data rows to the table
        for (Flat flat : flats) {
            int curr = Integer.parseInt(flat.getCurrentReading());
            int prev = Integer.parseInt(flat.getPreviousReading());
            int totalUnit = curr - prev;
            int waterBill = totalUnit * multiplier;
            int totalMaintenance = waterBill + fixMaintenance;

            table.addCell(flat.getFlatNumber());
            table.addCell(String.valueOf(prev));
            table.addCell(String.valueOf(curr));
            table.addCell(String.valueOf(totalUnit));
            table.addCell(String.valueOf(multiplier));
            table.addCell(String.valueOf(waterBill));
            table.addCell(String.valueOf(fixMaintenance));
            table.addCell(String.valueOf(totalMaintenance));
        }

        // Combine the heading table and main table
        Table finalTable = new Table(1);
        finalTable.addCell(new Cell().add(headingTable).setBorder(Border.NO_BORDER));
        finalTable.addCell(new Cell().add(table).setBorder(Border.NO_BORDER));

        return finalTable;
    }


    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // On Android 11+ (API level 30 and above), no need to check for WRITE_EXTERNAL_STORAGE permission
            return true;
        } else {
            // For Android 10 and below, check for WRITE_EXTERNAL_STORAGE permission
            return ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // No need to request WRITE_EXTERNAL_STORAGE on Android 11+
            Toast.makeText(this, "No permission required on Android 11+", Toast.LENGTH_SHORT).show();
        } else {
            // Request permission for Android 10 and below
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportFlatsToPdf();
                } else {
                    Toast.makeText(this, "Write permission is required to save the PDF", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_NOTIFICATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, you can show notifications
                    // Optionally, you can trigger a notification here if needed
                } else {
                    Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "pdf_export_channel";
            CharSequence name = "PDF Export Channel";
            String description = "Channel for PDF export notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String fileName, Uri pdfUri) {
        String channelId = "pdf_export_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the POST_NOTIFICATIONS permission is granted
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, do not show notification
                Toast.makeText(this, "Notification permission not granted", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logo2) // Replace with your app's notification icon
                .setContentTitle("PDF Exported")
                .setContentText("Your PDF " + fileName + " saved in Downloads")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

}