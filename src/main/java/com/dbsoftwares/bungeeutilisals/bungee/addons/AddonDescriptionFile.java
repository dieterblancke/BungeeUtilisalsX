package com.dbsoftwares.bungeeutilisals.bungee.addons;

/*
 * Created by DBSoftwares on 10 februari 2017
 * Developer: Dieter Blancke
 * Project: CMS
 * May only be used for CentrixPVP
 */

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Node;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class AddonDescriptionFile {

    private static ThreadLocal<Yaml> yml = ThreadLocal.withInitial(() -> new Yaml(new SafeConstructor() {{
            yamlConstructors.put(null, new AbstractConstruct() {
                @Override
                public Object construct(Node node) {
                    return SafeConstructor.undefinedConstructor.construct(node);
                }
            });
        }
    }));

    @Getter private String name = null;
    @Getter private List<String> depend = ImmutableList.of();
    @Getter private Integer version = null;
    private String main = null;

    public AddonDescriptionFile(InputStream stream) {
        this(new InputStreamReader(stream));
    }
    
    public AddonDescriptionFile(Reader reader) {
        loadMap((Map<?,?>) yml.get().load(reader));
    }
    
    public AddonDescriptionFile(String name, Integer version, String main) {
        this(name, version, main, ImmutableList.of());
    }

    public AddonDescriptionFile(String name, Integer version, String main, List<String> depend) {
        this.name = name.replace(' ', '_');
        this.version = version;
        this.main = main;
        this.depend = depend;
    }

    public void save(Writer writer) {
        yml.get().dump(saveMap(), writer);
    }

    private void loadMap(Map<?, ?> map) {
        name = map.get("name").toString().replace(' ', '_');
        main = map.get("main").toString();
        version = Integer.parseInt(map.get("version").toString());
        depend = makeAddonNameList(map, "depend");
    }

    private static List<String> makeAddonNameList(Map<?, ?> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return ImmutableList.of();
        }
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (Object entry : (Iterable<?>) value) {
            builder.add(entry.toString().replace(' ', '_'));
        }
        return builder.build();
    }

    public String getMain(){
        return main;
    }

    private Map<String, Object> saveMap() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", name);
        map.put("version", version);
        map.put("main", main);

        if (depend != null) {
            map.put("depend", depend);
        }

        return map;
    }
}
