package com.example.heartBuddy.Data;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class LocalObject <T extends Serializable> {

    Path path;
    private List<Consumer<LocalObject<T>>> onChangeHooks;
    public LocalObject (Path root, String... tags) {
        for (String tag : tags) {
            root = root.resolve(tag);
        }
        this.path = root;
        this.onChangeHooks = new ArrayList<>();
    }

    private void stage() {
        File f = this.path.toFile();
        if (!f.exists()) {
            f.getParentFile().mkdirs();
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public Optional<T> get(){
        this.stage();
        try {
            return Optional.ofNullable((T) new ObjectInputStream(new FileInputStream(this.path.toFile())).readObject());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public void put(T item) {
        this.stage();
        try {
            new ObjectOutputStream(new FileOutputStream(this.path.toFile())).writeObject(item);
            this.onChangeHooks.forEach(hook -> hook.accept(this));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void delete() {
        this.path.toFile().delete();
    }

    public void addHook(Consumer<LocalObject<T>> hook) {
        this.onChangeHooks.add(hook);
    }

    public void removeHook(Consumer<LocalObject<T>> hook) {
        this.onChangeHooks.remove(hook);
    }

}
