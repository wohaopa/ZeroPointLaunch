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

package com.github.wohaopa.zeropointlanuch.core.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.crypto.digest.DigestUtil;

public class FileUtil {

    /**
     * 获得并初始化目录
     *
     * @param parent 父文件夹对象
     * @param child  字文件夹路径
     * @return 文件夹对象
     */
    public static File initAndMkDir(File parent, String child) {
        File file = new File(parent, child);
        if (file.isFile()) if (!file.delete()) throw new RuntimeException("无法删除文件：" + file);
        if (!file.exists()) if (!file.mkdirs()) throw new RuntimeException("无法新建文件夹：" + file);
        return file;
    }

    /**
     * 读取文件
     *
     * @param file 文件对象
     * @return 文件内容
     */
    public static String fileRead(File file) {
        FileReader fr = new FileReader(file);
        return fr.readString();
    }

    /**
     * 写入文件（覆盖原内容）
     *
     * @param file    文件对象
     * @param content 写入内容
     */
    public static void fileWrite(File file, String content) {
        FileWriter fw = new FileWriter(file);
        fw.write(content);
    }

    /**
     * 将src->target
     *
     * @param src
     * @param target
     * @return 是否移动
     */
    public static boolean moveFile(File src, File target) {
        if (src.isFile()) {
            if (target.exists()) return false;
            if (!target.getParentFile().exists())
                if (!target.getParentFile().mkdirs()) throw new RuntimeException("无法创建文件夹：" + target.getParentFile());
            return src.renameTo(target);
        }
        return false;
    }

    /**
     * 删除链接文件
     *
     * @param file 目标文件
     */
    public static boolean isSymLink(File file) {
        Path path = file.toPath();
        try {
            return !path.equals(path.toRealPath());
        } catch (IOException e) {
            try {
                Files.delete(path); // 无法追溯源文件
                file.delete();
            } catch (IOException ignored) {}
            if (Files.exists(path)) throw new RuntimeException("无法删除文件：" + path);
            return false;
        }
    }

    /**
     * 检测文件sha1，hash不存在则为true，文件不存在则为false
     *
     * @param file 文件
     * @param hash sha1
     * @return
     */
    public static boolean checkSha1OfFile(File file, String hash) {
        if (!file.exists()) return false;
        if (hash == null || hash.isEmpty()) return true;
        return Objects.equals(DigestUtil.sha1Hex(file), hash);
    }

    public static boolean exists(File dir, String path) {
        return cn.hutool.core.io.FileUtil.exists(Path.of(dir.toString(), path), false);
    }
}
