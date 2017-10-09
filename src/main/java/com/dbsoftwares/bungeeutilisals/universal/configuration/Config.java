package com.dbsoftwares.bungeeutilisals.universal.configuration;

import com.google.common.collect.Lists;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

public class Config {
	
	private static List<Config> configs = Lists.newArrayList();
	private Object instance;
	public File file;
	public Configuration config;
	
	public Config(){
		configs.add(this);
	}
	
	public static void reloadConfigurations(){
		for(Config config : configs){
			config.reload();
		}
	}
	
	public void init(Object instance, File file){
		this.instance = instance;
		this.file = file;
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void reload(){
		try {
			this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		load();
	}
	
	public void save(){
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void set(String s, Object o){
		config.set(s, o);
		save();

		try {
			Field field = searchPath(s);
			if(field != null){
				field.set(instance, o);
			}
		} catch(Exception ignore){}
	}
	
	public void load(){
		Class<?> clazz = this.getClass();
		if (!clazz.getSuperclass().equals(Config.class)){
			clazz = clazz.getSuperclass();
		}
		for(Field field : clazz.getDeclaredFields()){
			field.setAccessible(true);
			for(Annotation a : field.getDeclaredAnnotations()){
				if(a instanceof ConfigPath){
					try {
						String path = ((ConfigPath)a).value();
						Object o = config.get(path);
						field.set(instance, o);
					} catch(Exception ignore){}
				}
			}
		}
	}
	
	public void loadDefaults() {
        Class<?> clazz = this.getClass();
        if (!clazz.getSuperclass().equals(Config.class)) {
            clazz = clazz.getSuperclass();
        }
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                String path = field.getDeclaredAnnotation(ConfigPath.class).value();
                Object def = field.get(instance);

                if (!config.contains(path)) {
                    config.set(path, def);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        save();
        load();
    }
	
	public String searchPath(Object o){
		try {
			Class<?> clazz = this.getClass();
			if (!clazz.getSuperclass().equals(Config.class)){
				clazz = clazz.getSuperclass();
			}
			for(Field field : clazz.getDeclaredFields()){
				field.setAccessible(true);
				for(Annotation a : field.getDeclaredAnnotations()){
					if(a instanceof ConfigPath){
						if(field.get(instance).equals(o)){
							return ((ConfigPath) a).value();
						}
					}
				}
			}
		} catch(Exception ignore){}
		return null;
	}
	
	private Field searchPath(String s){
		Class<?> clazz = this.getClass();
		if (!clazz.getSuperclass().equals(Config.class)){
			clazz = clazz.getSuperclass();
		}
		for(Field field : clazz.getDeclaredFields()){
			field.setAccessible(true);
			for(Annotation a : field.getDeclaredAnnotations()){
				if(a instanceof ConfigPath){
					String path = ((ConfigPath) a).value();
					if(path.equals(s)){
						return field;
					}
				}
			}
		}
		return null;
	}
}