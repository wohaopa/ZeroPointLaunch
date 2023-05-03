/*
 * MIT License
 * Copyright (c) 2023 初夏同学
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.wohaopa.zeropointlanuch.core;

import java.io.File;
import java.util.*;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import com.github.wohaopa.zeropointlanuch.core.utils.FileUtil;
import com.github.wohaopa.zeropointlanuch.core.utils.JsonUtil;

public class Mapper {

    private final Map<String, List<String>> exclude = new HashMap<>();
    private final Map<String, List<String>> include = new HashMap<>();
    // private List<String> all_include = new ArrayList<>();
    private final List<String> all_exclude;

    private final File targetDir;
    private final File config;

    public Mapper(File config, File targetDir) {
        List<String> all = new ArrayList<>();
        all.add("zpl_margi_config.json");
        exclude.put("__zpl_all__", all);
        this.targetDir = targetDir;
        this.config = config;
        this.analyticConfig();
        // this.all_include = include.getOrDefault("__zpl_all__", new ArrayList<>());
        this.all_exclude = exclude.get("__zpl_all__");
    }

    public File getTargetDir() {
        return targetDir;
    }

    private void analyticConfig() {
        JSONObject json = (JSONObject) JsonUtil.fromJson(config);
        List<_Config> exclude1 = json.getBeanList("exclude", _Config.class);
        exclude1.forEach(config -> {
            if (config.name == null) config.name = "__zpl_all__";
            List<String> list = exclude.computeIfAbsent(config.name, k -> new ArrayList<>());
            if (config.file != null) list.addAll(config.file);
        });

        List<_Config> include1 = json.getBeanList("include", _Config.class);
        include1.forEach(config -> {
            if (config.name == null) config.name = "__zpl_all__";
            List<String> list = include.computeIfAbsent(config.name, k -> new ArrayList<>());
            if (config.file != null) list.addAll(config.file);
        });
    }

    public void execute(String name, File dir) {
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                for (File file1 : Objects.requireNonNull(file.listFiles())) {
                    File tmp = new File(targetDir, file.getName() + "/" + file1.getName());
                    // doLink("__zpl_all__", tmp, file1);
                    doLink(name, tmp, file1);
                }
            } else {
                File tmp = new File(targetDir, file.getName());;
                // doLink("__zpl_all__", tmp, file);
                doLink(name, tmp, file);
            }
        }
    }

    public static JSONObject defaultJson() {
        JSONObject object = new JSONObject();
        object.putOpt("include", new JSONArray());
        object.putOpt("exclude", new JSONArray());
        return object;
    }

    private void doLink(String name, File link, File target) {
        List<String> include1 = include.get(name);
        String fileName = link.getPath();

        if (link.exists()) {
            if (include1 != null) {
                for (String str : include1) {
                    if (fileName.endsWith(str)) {
                        link.delete();
                        break;
                    }
                }
            }
            /*
             * 不需要全局包含，因为逻辑上默认全局包含
             * for (String str : all_include) {
             * if (fileName.endsWith(str)) {
             * link.delete();
             * break;
             * }
             * }
             */
        }

        List<String> exclude1 = exclude.get(name);
        if (exclude1 != null) {
            for (String str : exclude1) {
                if (fileName.endsWith(str)) {
                    return;
                }
            }
        }
        for (String str : all_exclude) {
            if (fileName.endsWith(str)) {
                return;
            }
        }

        FileUtil.genLink(link, target);
    }
}

class _Config {

    String name;
    List<String> file;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFile() {
        return file;
    }

    public void setFile(List<String> file) {
        this.file = file;
    }
}
