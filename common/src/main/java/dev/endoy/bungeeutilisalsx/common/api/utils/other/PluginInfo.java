package dev.endoy.bungeeutilisalsx.common.api.utils.other;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class PluginInfo
{

    private String name;
    private String version;
    private String author;
    private Set<String> depends;
    private Set<String> softDepends;
    private String description;

}
