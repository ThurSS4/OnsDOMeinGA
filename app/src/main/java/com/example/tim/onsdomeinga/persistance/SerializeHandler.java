package com.example.tim.onsdomeinga.persistance;

import android.content.Context;

import com.example.tim.onsdomeinga.controller.MainActivity;
import com.example.tim.onsdomeinga.model.Cluster;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SerializeHandler {

    private ArrayList<Cluster> clusterList = new ArrayList<>();
    private final String naam = "clusters";

    public void saveAsSerializedData(Context context) {

        clusterList = (ArrayList<Cluster>) MainActivity.getGa().getClusterList();
        try {
            saveClusterList(context);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void saveClusterList(Context context) throws IOException {

        FileOutputStream outputWriter = context.openFileOutput(naam, Context.MODE_PRIVATE);

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputWriter);
        objectOutputStream.writeObject(clusterList);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    public List<Cluster> loadFromSerializedData(Context context) throws IOException, ClassNotFoundException {
        return loadClusterList(context);
    }

    @SuppressWarnings("unchecked")
    private List<Cluster> loadClusterList(Context context) throws IOException, ClassNotFoundException {
        ArrayList<Cluster> clusterlist;

        FileInputStream inputReader = context.openFileInput(naam);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputReader);

        clusterlist = (ArrayList<Cluster>) objectInputStream.readObject();
        objectInputStream.close();

        for (Cluster cl : clusterList){
            cl.getdCom().setProxy();
        }

        return clusterlist;
    }
}
