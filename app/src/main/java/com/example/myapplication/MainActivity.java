package com.example.myapplication;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

//        PreferenceUtils.updateMultiplier(this, 25);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);



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
                if(isSavedSuccessfully) {
                    exportFlatsToPdf();
                }
            } else {
                requestPermission();
            }
        });

        // Set up save button click listener
        currentSaveButton.setOnClickListener(view -> saveAllReadings());

        // Load the default list (e.g., list 1)
        updateListView(1, button1);

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
            showSetMultiplierDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSetMultiplierDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Water rate: rs/KL");

        // Set up the input
        int multiplier = PreferenceUtils.getMultiplier(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(multiplier));
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = input.getText().toString();
                try {
                    int multiplier = Integer.parseInt(value);
                    PreferenceUtils.updateMultiplier(MainActivity.this, multiplier);
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Invalid number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

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
            selectedButton.setBackgroundColor(getResources().getColor(R.color.selected_button_color));
        }
        currentListType = listType; // Update the current list type
    }

    private void resetButtonColors() {
        button1.setBackgroundColor(getResources().getColor(R.color.default_button_color));
        button2.setBackgroundColor(getResources().getColor(R.color.default_button_color));
        button3.setBackgroundColor(getResources().getColor(R.color.default_button_color));
        button4.setBackgroundColor(getResources().getColor(R.color.default_button_color));
    }

    private boolean saveAllReadings() {
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

            Toast.makeText(this, "All readings saved", Toast.LENGTH_SHORT).show();
            updateListView(currentListType, getSelectedButton()); // Refresh the ListView to reflect updated data
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
                return "siddhA";
            case 2:
                return "siddhB";
            case 3:
                return "sargamA";
            case 4:
                return "sargamB";
            default:
                return "unknown";
        }
    }

    private void exportFlatsToPdf() {
        List<Flat> flats = flatAdapter.getFlats(); // Get the current flats from the adapter

        // Create a new PdfDocument
        PdfDocument pdfDocument = new PdfDocument();

        // Paint object to customize text appearance and lines
        Paint paint = new Paint();
        paint.setTextSize(12);
        paint.setStyle(Paint.Style.STROKE); // Set style to STROKE for lines

        Paint textPaint = new Paint();
        textPaint.setTextSize(12);
        textPaint.setTextAlign(Paint.Align.LEFT);

        // Create a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(400, 600, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        int pageWidth = pageInfo.getPageWidth();
        int x = 30; // Starting x coordinate for the table
        int y = 25;
        int rowHeight = 25; // Height of each row
        int lineSpacing = 5; // Adjusted spacing for text inside the cells

        // Define column widths
        int flatNumberColWidth = 60;
        int previousReadingColWidth = 80;
        int currentReadingColWidth = 80;
        int totalMaintenanceColWidth = 80;

        // Total width of the table
        int tableWidth = flatNumberColWidth + previousReadingColWidth + currentReadingColWidth + totalMaintenanceColWidth;

        // Center the table on the page
        x = (pageWidth - tableWidth - 20) / 2; // 20 for padding

        String listName = getListName();
        textPaint.setTextSize(16); // Larger font size for the list name
        canvas.drawText(listName, (pageWidth - textPaint.measureText(listName)) / 2, y + 20, textPaint);

        // Draw the header row
        int headerY = y + rowHeight / 2 + 4; // Adjust vertical alignment

        canvas.drawRect(x, y, x + tableWidth, y + rowHeight, paint); // Draw a rectangle for the header row
        canvas.drawText("Flat No.", x + lineSpacing, headerY, textPaint);
        canvas.drawText("Prev. Reading", x + flatNumberColWidth + lineSpacing, headerY, textPaint);
        canvas.drawText("Curr. Reading", x + flatNumberColWidth + previousReadingColWidth + lineSpacing, headerY, textPaint);
        canvas.drawText("Total Maint.", x + flatNumberColWidth + previousReadingColWidth + currentReadingColWidth + lineSpacing, headerY, textPaint);

        // Draw vertical lines for columns
        int currentX = x;
        canvas.drawLine(currentX, y, currentX, y + rowHeight * (flats.size() + 2), paint); // Left border
        currentX += flatNumberColWidth;
        canvas.drawLine(currentX, y, currentX, y + rowHeight * (flats.size() + 2), paint);
        currentX += previousReadingColWidth;
        canvas.drawLine(currentX, y, currentX, y + rowHeight * (flats.size() + 2), paint);
        currentX += currentReadingColWidth;
        canvas.drawLine(currentX, y, currentX, y + rowHeight * (flats.size() + 2), paint);
        currentX += totalMaintenanceColWidth;
        canvas.drawLine(currentX, y, currentX, y + rowHeight * (flats.size() + 2), paint); // Right border

        y += rowHeight; // Move to the next row

        // Draw each data row
        for (Flat flat : flats) {
            canvas.drawRect(x, y, x + tableWidth, y + rowHeight, paint); // Draw a rectangle for each data row

            // Draw text inside the cells
            canvas.drawText(flat.getFlatNumber(), x + lineSpacing, y + rowHeight / 2 + 4, textPaint);
            canvas.drawText(flat.getPreviousReading(), x + flatNumberColWidth + lineSpacing, y + rowHeight / 2 + 4, textPaint);
            canvas.drawText(flat.getCurrentReading(), x + flatNumberColWidth + previousReadingColWidth + lineSpacing, y + rowHeight / 2 + 4, textPaint);
            canvas.drawText(flat.getTotalMaintenance(), x + flatNumberColWidth + previousReadingColWidth + currentReadingColWidth + lineSpacing, y + rowHeight / 2 + 4, textPaint);

            y += rowHeight; // Move to the next row
        }

        // Finish the page
        pdfDocument.finishPage(page);

        // Get current date in MonthName_Date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM_dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        String fileName = getListName() + "_" + currentDate + "_records.pdf";

        FileOutputStream outStream = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // On Android 10 and above, use MediaStore to save the PDF
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

            if (uri != null) {
                try {
                    outStream = (FileOutputStream) getContentResolver().openOutputStream(uri);
                    pdfDocument.writeTo(outStream);
                    Toast.makeText(this, "PDF saved successfully", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } finally {
                    pdfDocument.close();
                }
            }
        } else {
            // For older versions, save the PDF using traditional file handling
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File filePath = new File(downloadsDir, fileName);
            try {
                outStream = new FileOutputStream(filePath);
                pdfDocument.writeTo(outStream);
                Toast.makeText(this, "PDF saved at " + filePath.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                pdfDocument.close();
            }
        }
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
        }    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportFlatsToPdf();
            } else {
                Toast.makeText(this, "Write permission is required to save the PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }
}