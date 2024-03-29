module ZeroPointLaunch.Core {
  requires java.sql;
  requires org.apache.commons.compress;
  requires cn.hutool.core;
  requires cn.hutool.json;
  requires cn.hutool.http;
  requires cn.hutool.crypto;
  requires cn.hutool.extra;
  requires org.apache.logging.log4j.core;
  requires org.apache.logging.log4j;

  exports com.github.wohaopa.zeropointlanuch.core;
  exports com.github.wohaopa.zeropointlanuch.core.filesystem;
  exports com.github.wohaopa.zeropointlanuch.core.download;
  exports com.github.wohaopa.zeropointlanuch.core.auth;
  exports com.github.wohaopa.zeropointlanuch.core.tasks;
  exports com.github.wohaopa.zeropointlanuch.core.launch;
  exports com.github.wohaopa.zeropointlanuch.core.tasks.instances;
  exports com.github.wohaopa.zeropointlanuch.core.utils;
}
