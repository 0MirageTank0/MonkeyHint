package org.monkey.data;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@State(
        name = "MapDataStorage",
        storages = @Storage("DataStorage.xml")
)
public class MapDataStorage implements PersistentStateComponent<MapDataStorage> {

    private String data;


    // Key : [ ["member1","type","comment"],["member2","type","comment"]... ]
    private transient final Map<String,List<String[]>> cacheData = new HashMap<>();


    public String getData(){
        return data;
    }

    public Map<String, List<String[]>> getCacheData() {
        return cacheData;
    }




    public void updateCache(Map<String, List<String>> dataMap){
        cacheData.clear();
        for (var entry : dataMap.entrySet()) {
            var entryValue = entry.getValue();
            var membersData = new ArrayList<String[]>(entryValue.size());
            for (String s : entryValue) {
                String[] commentSplit = s.split("#", 2);
                String[] typeSplit = commentSplit[0].trim().split(":", 2);

                if (!typeSplit[0].isEmpty()) {
                    var type = entry.getKey();
                    var comment = "";
                    if(commentSplit.length == 2)
                        comment = commentSplit[1].trim();
                    if (typeSplit.length == 2)
                        type = typeSplit[1].trim();
                    typeSplit[0] = typeSplit[0].trim();
                    membersData.add(new String[]{typeSplit[0], type, comment});
                }
            }
            cacheData.put(entry.getKey(),membersData);
        }
    }

    public Map<String, List<String>> calculateMapData(){
        String[] lines = data.split("\n");
        Map<String, List<String>> dataMap = new HashMap<>();
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                String[] split = line.split("\\.", 2);
                List<String> memberList = dataMap.computeIfAbsent(split[0], k -> new ArrayList<>());
                memberList.add(split[1]);
            }
        }
        return dataMap;
    }


    public void setData(String data){
        this.data = data;
    }

    @Nullable
    @Override
    public MapDataStorage getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull MapDataStorage state) {
        data = state.data;
        updateCache(calculateMapData());
    }

    public static MapDataStorage getInstance(@NotNull Project project) {
        return project.getService(MapDataStorage.class);
    }
}
