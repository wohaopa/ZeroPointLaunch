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

package com.github.wohaopa.zeropointlanuch.core.tasks.instances;

import java.io.File;
import java.util.function.Consumer;

import com.github.wohaopa.zeropointlanuch.core.Instance;
import com.github.wohaopa.zeropointlanuch.core.Log;
import com.github.wohaopa.zeropointlanuch.core.ZplDirectory;
import com.github.wohaopa.zeropointlanuch.core.tasks.DecompressTask;
import com.github.wohaopa.zeropointlanuch.core.tasks.Task;

public class ZplInstallTask extends Task<Instance> {

    protected File zip;
    protected File instanceDir;
    protected String name;

    public ZplInstallTask(File zip, File instanceDir, String name, Consumer<String> callback) {
        super(callback);
        this.zip = zip;
        this.instanceDir = instanceDir;
        this.name = name;
    }

    @Override
    public Instance call() throws Exception {

        if (Instance.containsKey(name)) {
            accept("存在实例：" + name);
            Log.info("存在实例：{}", name);
            return Instance.get(name);
        }

        new DecompressTask(zip, instanceDir, callback).call();

        Instance.Builder builder = new Instance.Builder(new File(instanceDir, "version.json"));
        builder.setName(name).saveConfig();
        Instance instance = builder.build();
        String dep = instance.information.depVersion;
        if (dep != null && !dep.equals("null")) {
            accept("正在处理依赖：" + dep);
            File dir = new File(ZplDirectory.getInstancesDirectory(), dep);
            new OnlineInstallTask(dir, dep, callback).call();
        }

        accept("已完成安装：" + name);
        return instance;
    }
}
