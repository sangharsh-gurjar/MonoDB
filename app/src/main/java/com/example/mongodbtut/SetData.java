package com.example.mongodbtut;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.bson.Document;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.sync.SyncConfiguration;

public class SetData extends AppCompatActivity {
    EditText StudentId;
    EditText StudentName;
    EditText StudentAddress;
    EditText StudentEmail;
    Button Submit;
    Realm uiThreadRealm;
    App app;
    String appID ="application-0-tnijw";
    Task task1 = new Task("taskk");
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_data);

        StudentId=findViewById(R.id.StudentId);
        StudentName=findViewById(R.id.StudentName);
        StudentAddress=findViewById(R.id.StudentAddress);
        StudentEmail=findViewById(R.id.StudentEmail);
        Submit =findViewById(R.id.Submit);
        Realm.init(this);

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a entry in database

               // ObjectId _id = new ObjectId(StudentId.getText().toString());
                //task1.set_id(_id);
//                task1.setName(StudentName.getText().toString());
//                task1.setAddress(StudentAddress.getText().toString());
//                task1.setEmail(StudentEmail.getText().toString());


                 // context, usually an Activity or Application
                app = new App(new AppConfiguration.Builder(appID)
                        .build());
                Credentials credentials = Credentials.anonymous();
                app.loginAsync(credentials, result -> {
                    if (result.isSuccess()) {
                        Log.v("QUICKSTART", "Successfully authenticated anonymously.");
                        User user = app.currentUser();
                        mongoClient = user.getMongoClient("mongobd-atlas");
                        mongoDatabase=mongoClient.getDatabase("Student");
                        MongoCollection<Document> mongoCollection= mongoDatabase.getCollection("Student1");
                        mongoCollection.insertOne(new Document("userId",user.getId()).append("name",StudentName.getText().toString())
                                .append("email",StudentEmail.getText().toString()).append("address",StudentAddress.getText().toString()))
                                .getAsync(result1 -> {
                            if (result1.isSuccess()){
                                Log.v("DATABASE", "Inserted Successfully");
                            }
                            else{
                                Log.v("DATABASE--", result1.getError().toString());
                            }
                        });

                    } else {
                        Log.e("QUICKSTART", "Failed to log in. Error: " + result.getError());
                    }
                });


            }
        });




    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // the ui thread realm uses asynchronous transactions, so we can only safely close the realm
        // when the activity ends and we can safely assume that those transactions have completed
        uiThreadRealm.close();
        app.currentUser().logOutAsync(result -> {
            if (result.isSuccess()) {
                Log.v("QUICKSTART", "Successfully logged out.");
            } else {
                Log.e("QUICKSTART", "Failed to log out, error: " + result.getError());
            }
        });
    }
    public class BackgroundQuickStart implements Runnable {
        User user;
        Task task;
        public BackgroundQuickStart(User user) {
            this.user = user;
            this.task=task1;

        }



        @Override
        public void run() {
            String partitionValue = "My Project";
            SyncConfiguration config = new SyncConfiguration.Builder(
                    user,
                    partitionValue)
                    .build();
            Realm backgroundThreadRealm = Realm.getInstance(config);
            backgroundThreadRealm.executeTransaction (transactionRealm -> {
                transactionRealm.insert(task);
                Log.v("DATABASE", "inserted data successfully");
            });



            /*
            // all tasks in the realm
            RealmResults<Task> tasks = backgroundThreadRealm.where(Task.class).findAll();
            // you can also filter a collection
            RealmResults<Task> tasksThatBeginWithN = tasks.where().beginsWith("name", "N").findAll();
            RealmResults<Task> openTasks = tasks.where().equalTo("status", TaskStatus.Open.name()).findAll();
            Task otherTask = tasks.get(0);
            // all modifications to a realm must happen inside of a write block
            backgroundThreadRealm.executeTransaction( transactionRealm -> {
                Task innerOtherTask = transactionRealm.where(Task.class).equalTo("_id", otherTask.get_id()).findFirst();
                innerOtherTask.setStatus(TaskStatus.Complete);
            });
            Task yetAnotherTask = tasks.get(0);
            ObjectId yetAnotherTaskId = yetAnotherTask.get_id();
            // all modifications to a realm must happen inside of a write block
            backgroundThreadRealm.executeTransaction( transactionRealm -> {
                Task innerYetAnotherTask = transactionRealm.where(Task.class).equalTo("_id", yetAnotherTaskId).findFirst();
                innerYetAnotherTask.deleteFromRealm();
            });
            // because this background thread uses synchronous realm transactions, at this point all
            // transactions have completed and we can safely close the realm

             */
            backgroundThreadRealm.close();
        }
    }


}