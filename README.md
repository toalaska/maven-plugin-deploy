 # 使用方法
 
## 代码统计 
    
    <plugin>
        <groupId>cn.toalaska</groupId>
        <artifactId>deploy</artifactId>
        <version>1.0-SNAPSHOT</version>
        <configuration>
            <basedir>${basedir}</basedir>
        </configuration>
        <executions>
            <execution>
                <!-- phase表示运行在哪个阶段 -->
                <phase>clean</phase>
                <goals>
                    <!--goal标识运行哪个命令，可以这样认为-->
                    <goal>filecount</goal>
                </goals>
            </execution>
        </executions>
    </plugin>


## 部署文件
    
    <plugin>
        <groupId>cn.toalaska</groupId>
        <artifactId>deploy</artifactId>
        <version>1.10-SNAPSHOT</version>
        <configuration>
            <basedir>${basedir}</basedir>
            <host>192.168.199.241</host>
            <id_rsa_path>C:\Users\Myra\.ssh\id_rsa</id_rsa_path>
            <files>nh.png:/tmp/dddd/nh.png</files>
        </configuration>
        <executions>
            <execution>
                <!-- phase表示运行在哪个阶段 -->
                <phase>clean</phase>
                <goals>
                    <!--goal标识运行哪个命令，可以这样认为-->
                    <goal>deploy</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
