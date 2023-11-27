package com.example.gestordetareas;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {
    private TaskDbHelper dbHelper;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TaskDbHelper(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskAdapter = new TaskAdapter(readTasks(), this);
        recyclerView.setAdapter(taskAdapter);

        registerForContextMenu(recyclerView);

        Button addButton = findViewById(R.id.buttonAddTask);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.editTextTask);
                String task = editText.getText().toString();

                if (!task.isEmpty()) {
                    addTask(task);
                    taskAdapter.setTasks(readTasks());
                    editText.setText("");
                } else {
                    // Mostrar mensaje de error si el campo está vacío
                    Toast.makeText(MainActivity.this, "El campo no puede estar vacío", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addTask(String task) {
        dbHelper.insertTask(task);
    }

    private Cursor readTasks() {
        return dbHelper.getAllTasks();
    }

    @Override
    public void onTaskClick(int position) {
        // Manejar clic en tarea (si es necesario)
    }

    @Override
    public void onTaskLongClick(int position) {
        taskAdapter.setSelectedPosition(position);
        openContextMenu(findViewById(R.id.recyclerViewTasks));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = taskAdapter.getSelectedPosition();
        if (position != RecyclerView.NO_POSITION) {
            if (item.getItemId() == R.id.menu_update) {
                // Modificar tarea
                showUpdateDialog(position);
            } else if (item.getItemId() == R.id.menu_delete) {
                // Eliminar tarea
                dbHelper.deleteTask(getTaskIdAtPosition(position));
                taskAdapter.setTasks(readTasks());
            }
        }
        return super.onContextItemSelected(item);
    }

    private long getTaskIdAtPosition(int position) {
        Cursor cursor = taskAdapter.getCursor();
        cursor.moveToPosition(position);
        return cursor.getLong(cursor.getColumnIndex(TaskDbHelper.COLUMN_ID));
    }

    private void showUpdateDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modificar Tarea");

        // Obtener el texto actual de la tarea
        String currentTask = getTaskTextAtPosition(position);

        // Crear un EditText con el texto actual
        final EditText input = new EditText(this);
        input.setText(currentTask);

        builder.setView(input);
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newTask = input.getText().toString().trim();

                // Verificar que la tarea no esté vacía antes de guardar
                if (!newTask.isEmpty()) {
                    dbHelper.updateTask(getTaskIdAtPosition(position), newTask);
                    taskAdapter.setTasks(readTasks());
                } else {
                    // Mostrar un mensaje de error si la tarea está vacía
                    Toast.makeText(MainActivity.this, "La tarea no puede estar vacía", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private String getTaskTextAtPosition(int position) {
        Cursor cursor = taskAdapter.getCursor();
        cursor.moveToPosition(position);
        return cursor.getString(cursor.getColumnIndex(TaskDbHelper.COLUMN_TASK));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
