package com.example.mongodbtut;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.SyncConfiguration;

public class GetData extends AppCompatActivity {
    Realm uiThreadRealm;
    App app;
    String appID ="application-0-tnijw";
    RecyclerView StudentList;
    Task []data;
    boolean dataFetched =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_data);
        StudentList=findViewById(R.id.StudentList);
        StudentList.setLayoutManager(new LinearLayoutManager(this));


        Realm.init(this);
        app = new App(new AppConfiguration.Builder(appID)
                .build());
        Credentials credentials = Credentials.anonymous();
        app.loginAsync(credentials, result -> {
            if (result.isSuccess()) {
                Log.v("QUICKSTART", "Successfully authenticated anonymously.");
                User user = app.currentUser();
                String partitionValue = "My Project";
                SyncConfiguration config = new SyncConfiguration.Builder(
                        user,
                        partitionValue)
                        .build();
                uiThreadRealm = Realm.getInstance(config);
                //addChangeListenerToRealm(uiThreadRealm);
                FutureTask<String> task = new FutureTask(new GetData.BackgroundQuickStart(app.currentUser()), "test");
                ExecutorService executorService = Executors.newFixedThreadPool(2);
                executorService.execute(task);
            } else {
                Log.e("QUICKSTART", "Failed to log in. Error: " + result.getError());
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

        }


        @Override
        public void run() {
            String partitionValue = "My Project";
            SyncConfiguration config = new SyncConfiguration.Builder(
                    user,
                    partitionValue)
                    .build();
            Realm backgroundThreadRealm = Realm.getInstance(config);
            RealmResults<Task> tasks = backgroundThreadRealm.where(Task.class).findAll();
            data = new Task[tasks.size()];
            for(int i=0;i<tasks.size();i++){
                data[i] = tasks.get(i);
                dataFetched=true;
            }
            if(dataFetched){
                StudentList.setAdapter(new StudentAdapter(data));
                Log.v("DATABASE", "fetched data successfully");
            }
            if(tasks.isEmpty()){
                Log.v("DATABASE", "No data present");

            }







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