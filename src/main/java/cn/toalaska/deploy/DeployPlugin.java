package cn.toalaska.deploy;

import com.jcraft.jsch.*;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by xingyuchao on 2017-06-03.
 * 统计以特定格式文件总数
 */
@Mojo(name = "deploy", defaultPhase = LifecyclePhase.PACKAGE)
public class DeployPlugin extends AbstractMojo {


    //获取文件路径
    @Parameter
    private String basedir;


    @Parameter(property = "host")
    private String host;

    @Parameter(property = "port")
    private int port = 22;

    @Parameter(property = "user")
    private String user = "root";

    @Parameter(property = "psw")
    private String psw;

    @Parameter(property = "id_rsa_path")
    private String id_rsa_path;


    @Parameter(property = "files")
    private String files;


    @Parameter(property = "cmd")
    private String cmd;
    private ArrayList<TransTask> transTasks;
    private ChannelSftp channelSftp;


    public static class TransTask {
        private File src;
        private File dst;

        public TransTask(File src, File dst) {
            this.src = src;
            this.dst = dst;
        }

        public File getSrc() {
            return src;
        }

        public void setSrc(File src) {
            this.src = src;
        }

        public File getDst() {
            return dst;
        }

        public void setDst(File dst) {
            this.dst = dst;
        }
    }

    public void execute() throws MojoExecutionException, MojoFailureException {

        check();
        JSch jsch = new JSch();

        Session session = null;
        try {
            session = jsch.getSession(user, host, port);

            if (!StringUtils.isBlank(psw)) {
                session.setPassword(psw);
            } else {
                jsch.addIdentity(id_rsa_path);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();


            channelSftp = (ChannelSftp) session.openChannel("sftp");

            channelSftp.connect();

//            try {
//                channelSftp.setFilenameEncoding("utf-8");
//            }catch (Exception e){
//                System.out.println("setFilenameEncoding fail");
//            }
            for (TransTask transTask : transTasks) {
                System.out.println("正在上传：" + transTask.getSrc().getAbsolutePath());
                uploadFile(transTask.getSrc(), transTask.getDst());
            }

        } catch (JSchException e) {
            e.printStackTrace();
            throw new MojoFailureException("sftp错误" + e.getMessage());
        } catch (SftpException e) {
            e.printStackTrace();

            throw new MojoFailureException("sftp错误" + e.getMessage());


        } catch (Exception e) {
            e.printStackTrace();
            throw new MojoFailureException("上传文件失败" + e.getMessage());

        }
    }

    private void check() throws MojoFailureException {
        System.out.println("host:" + host);
        System.out.println("user:" + user);
        System.out.println("psw:" + psw);
        System.out.println("id_rsa_path:" + id_rsa_path);
        System.out.println("files:" + files);

        if (StringUtils.isBlank(host)) {
            throw new MojoFailureException("host配置错误");
        }
        if (StringUtils.isBlank(user)) {
            throw new MojoFailureException("user配置错误");
        }
        if (port <= 0) {
            throw new MojoFailureException("port配置错误");

        }
        if (StringUtils.isBlank(psw) && StringUtils.isBlank(id_rsa_path)) {
            throw new MojoFailureException("密码和密钥必须配置至少配置一个");
        }
        if (StringUtils.isBlank(files)) {
            throw new MojoFailureException("待上传的文件不能为空");

        }
        transTasks = new ArrayList<TransTask>();
        for (String s : files.split(";")) {
            String[] arr = s.split(":");
            if (arr.length != 2) {
                throw new MojoFailureException("文件配置错误：" + s);
            }
            File src = new File(arr[0]);
            if (!src.exists()) {
                throw new MojoFailureException("文件不存在：" + src.getAbsolutePath());
            }

            transTasks.add(new TransTask(src, new File(arr[1])));
        }
    }

    public void uploadFile(File localFile, File remoteFile) throws Exception {
        InputStream input = null;
        try {
            input = new FileInputStream(localFile);

            String dstPath = remoteFile.getParent().replace("\\", "/");
            System.out.println("dstPath=" + dstPath);

            try {
                SftpATTRS lstat = channelSftp.lstat(dstPath);
            } catch (Exception e) {
//                e.printStackTrace();
                System.out.println("自动创建目录：" + dstPath);
                channelSftp.mkdir(dstPath);
            }
            System.out.println("cd " + dstPath);

            // 改变当前路径到指定路径
            channelSftp.cd(dstPath);
            System.out.println("开始上传 " + remoteFile.getName());

            channelSftp.put(input, remoteFile.getName());
        } catch (SftpException e) {
            e.printStackTrace();
            throw new Exception("sftp异常：" + remoteFile.getAbsolutePath() + e.getMessage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            throw new Exception("文件不存在：" + localFile.getAbsolutePath() + e.getMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    throw new Exception("Close stream error：" + localFile.getAbsolutePath());
                }
            }
        }
    }


}
