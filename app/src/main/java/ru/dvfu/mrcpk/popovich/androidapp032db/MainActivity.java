package ru.dvfu.mrcpk.popovich.androidapp032db;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    final int REQUEST_CODE_NAME = 1;
    final int REQUEST_CODE_ADDNAME = 2;


    List names ;
    List idNames;
    ListView listView;
    Button buttonEdit;
    Button buttonAdd;
    Button buttonDel;
    ArrayAdapter<String> adapterNames;

    //Переменная для инициализации DB
    SQLHelper sqlHelper;
    //Для управления DB - query(),insert(),delete(),update(), execSQL()
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Массив для данных персон
        names = new ArrayList<String>();
        idNames = new ArrayList<Integer>();

        //Экземпляр класса DB
        sqlHelper = new SQLHelper(this);
        //объект класса для получения доступа к управлению с поддержкой записи данных
        sqLiteDatabase = sqlHelper.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.query(SQLHelper.TABLE_MAIN,null,null,null,null,null,null);

        if(cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex("_id");
            int firstnameColIndex = cursor.getColumnIndex("firstname");
            int lastnameColIndex = cursor.getColumnIndex("lastname");
            do {
                names.add(cursor.getString(firstnameColIndex) + " " + cursor.getString(lastnameColIndex));
                idNames.add(cursor.getInt(idColIndex));
                Log.d("myLog", "ID = " + cursor.getInt(idColIndex) + " , firstName = " + cursor.getString(firstnameColIndex));
            } while (cursor.moveToNext());
        }
        cursor.close();

        //Определение списка из макета activity_main.xml
        listView = (ListView) findViewById(R.id.listView);

        // Установка режима выбора пунктов списка (по одному)
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        //Создание адаптера из массива names для назначения их списку (напрямую нельзя!)
        adapterNames = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice, names);
        listView.setAdapter(adapterNames);

        //Объекты кнопок редактирования, добавления, удаления
        buttonEdit = (Button) findViewById(R.id.buttonEdit);
        buttonEdit.setOnClickListener(this);
        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(this);
        buttonDel = (Button) findViewById(R.id.buttonDel);
        buttonDel.setOnClickListener(this);

        //Обработка выбора пункта списка (НЕОБЯЗАТЕЛЬНАЯ ЧАСТЬ)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this,names.get(listView.getCheckedItemPosition()) + ", pos = " + position + ", id = " + id + " , _id = " + idNames.get(position),Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this,names.get(listView.getCheckedItemPosition()) + ", pos = " + position + ", id = " + id,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()){
            case R.id.buttonEdit:
                //Создание объекта "намерение" для порождения нового активити - EditActivity
                intent = new Intent(this, EditActivity.class);

                // Работа с элементом списка. Если не выбран ни один пунк - выдать сообщение (Toast)
                if(listView.getCheckedItemPosition() != -1) {
                    intent.putExtra("desc","Редактировать данные:");
                    intent.putExtra("firstname", String.valueOf(names.get(listView.getCheckedItemPosition())));
                    Toast.makeText(this, String.valueOf(names.get(listView.getCheckedItemPosition())), Toast.LENGTH_LONG).show();
//                startActivity(intent);
                    startActivityForResult(intent, REQUEST_CODE_NAME);
                } else {
                    Toast.makeText(this, "Выберите пунк списка", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonAdd:
                intent = new Intent(this, EditActivity.class);
                intent.putExtra("firstname",false);
                intent.putExtra("lastname",false);
                intent.putExtra("desc","Добавить персону:");
                startActivityForResult(intent, REQUEST_CODE_ADDNAME);
                break;
            case R.id.buttonDel:
                names.remove(listView.getCheckedItemPosition());

                sqLiteDatabase.delete(SQLHelper.TABLE_MAIN, SQLHelper.TABLE_MAIN_ID + " = " + idNames.get(listView.getCheckedItemPosition()), null);
                Log.d("myLog", "delete ID = " + idNames.get(listView.getCheckedItemPosition()));
                listView.setAdapter(adapterNames);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(data == null) return;
        if(resultCode == RESULT_OK) {
            switch (requestCode){
                case REQUEST_CODE_NAME:
                    String firstnameResult = data.getExtras().getString("firstname");
                    names.set(listView.getCheckedItemPosition(),firstnameResult);
                    Toast.makeText(MainActivity.this, firstnameResult, Toast.LENGTH_LONG).show();
                    listView.setAdapter(adapterNames);
                    //                textViewName.setText(nameResult);
                    break;
                case REQUEST_CODE_ADDNAME:
                    String firstnameAddResult = data.getExtras().getString("firstname");
                    String lastnameAddResult = data.getExtras().getString("lastname");

                    //Переменная для массива данных в виде MAP (ключ,значение)
                    ContentValues contentValues=new ContentValues();

                    //Заполнение возвращаемых данных по принципу КЛЮЧ-Значение
                    contentValues.put(SQLHelper.TABLE_MAIN_FIRSTNAME, firstnameAddResult);
                    contentValues.put(SQLHelper.TABLE_MAIN_LASTNAME, lastnameAddResult);
                    sqLiteDatabase.insert(SQLHelper.TABLE_MAIN,null,contentValues);

                    names.add(firstnameAddResult + " " + lastnameAddResult);

                    Toast.makeText(MainActivity.this, firstnameAddResult + " " + lastnameAddResult, Toast.LENGTH_LONG).show();
                    listView.setAdapter(adapterNames);
                    break;
            }
        }

    }
}
