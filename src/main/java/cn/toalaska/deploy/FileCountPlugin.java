package cn.toalaska.deploy;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xingyuchao on 2017-06-03.
 * 统计以特定格式文件总数
 */
@Mojo(name = "filecount",defaultPhase = LifecyclePhase.PACKAGE)
public class FileCountPlugin extends AbstractMojo {


    private static List<String> fileList = new ArrayList<String>();

    //获取文件路径
    @Parameter
    private String basedir;

    //定义文件后缀   如果要传递 Args的参数需要这样传：mvn clean -Dsuffix=.xml
    @Parameter(property = "suffix",defaultValue = ".java")
    private String defaultFileSuffix;

    //定义文件作为参数传递的方式，可不传递
    @Parameter(property = "args")
    private String fileSuffix;

    public void execute() throws MojoExecutionException, MojoFailureException {
        List<String> fileList = scanFile(basedir);
        System.out.println("FilePath:"+basedir);
        System.out.println("fileSuffix:"+fileSuffix);
        System.out.println("defaultFileSuffix:"+defaultFileSuffix);
        System.out.println("FileTotal:"+fileList.size());
    }


    /**
     * 递归统计文件，并将符合条件的文件放入集合中
     * @param filePath
     * @return
     */
    private List<String> scanFile(String filePath) {
        File dir = new File(filePath);
        // 递归查找到所有的class文件
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                scanFile(file.getAbsolutePath());
            } else {
                //以运行参数方式传递作为有限判断条件
                if(fileSuffix != null && !"".equals(fileSuffix)){
                    if(file.getName().endsWith(fileSuffix)){
                        fileList.add(file.getName());
                    }
                }else if(defaultFileSuffix != null && !"".equals(defaultFileSuffix)){
                    //如果优先条件没有传，则以xml中配置为标准
                    if(file.getName().endsWith(defaultFileSuffix)){
                        fileList.add(file.getName());
                    }
                }
            }
        }
        return fileList;
    }
}
