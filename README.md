START
=====
车辆移动模型，全称是 START: Status and Region Aware Taxi Mobility Model

基于北京市出租车数据，对不同状态下的出租车行为进行讨论。
基于One仿真平台。

开始是基于RWP移动模型
然后是基于ShortestPathMovementModel

以下是主要类的说明。

将英语译成中文
全一1.2 - 自述文件 

全-1.4.1readme文件--kid重翻，新增部分内容(by 欧阳)欢迎更正~
===================== 

    ONE是一个机会网络环境模拟器提供了一个强大的工具来追踪移动节点，运行不同路由策略的消息发送脚本，和模拟进行时或者模拟完成后的可视化界面。 


快速启动 
=========== 

编译 
--------- 

    在装有java 6 JDK或更高版本的Windows和Unix / Linux环境下，直接调用ONE源文件中的compile.bat脚本对ONE进行编译。

    如果你想使用Eclipse 1.1.0来编译，你需要 
在项目的构建路径中添加若干JAR库。这些库 
位于lib文件夹。将它们包括进去以后，假设你拥有 
一个Eclipse Java项目的根文件夹是您提取 
ONE文件，按下面步骤进行： 
  
    从菜单选择：项目 - >属性-> Java路径 
    转到“Libraries”标签 
    点击“添加JAR文件...” 
    选择“DTNConsoleConnection.jar”下的“库”文件夹 
    以同样的方式舔加“ECLA.jar”
    按“OK”。 

    现在，Eclipse应该能够编译ONE而不产生warings。 


运行 
------- 

一开始可以使用附带的one.bat（Windows）或one.sh（对于Linux / Unix操作系统）。下面的例子假设你使用的Linux / Unix（对windows而言./one.sh由one.bat代替）。 

简介： 
./one.sh [-b runcount] [conf-files]

选项： 
  - b以批处理模式运行仿真。不启动图形界面，但打印程序运行的 
信息到终端。 这个选项必须设置运行的次数或者运行的次数范围，由一个冒号分开（e.g.值2:4表示执行2,3,4）。在"Run indexing"中又更详细的说明。
   
参数： 
conf-files：指定仿真参数由哪个配置文件读取。可以定义多个配置文件，运行时按照命令的先后执行。后一个（同名）配置文件的参数将覆盖前面的配置文件。 

配置 
=========== 

    所有模拟参数为由配置文件给出。文件由成对的key、value构成的文本文件。变量的基本语法为： 
Namespace.key =value 

也就是说，key（通常）有个namespace的前缀，用点分开，然后key的名称。键和值之间用等于符号的。key和value用等号分开。
namespace第一个字母大写，namespace和key以驼峰命名法（　在英语中，依靠单词的大小写拼写复合词的做法，叫做“骆驼拼写法”CamelCase）书写（区分大小写）。namespace的命名一般与仿真环境相关。大多数的namespace与类名命名相同，音义即表示的意义。 
特别是运动模式，模块报告和路由模块的命名都遵循此规则。 
数字值分隔符'.'作分隔，可后缀 kilo（k）mega（M）或giga(千兆，十亿)（G）的后缀。布尔设置接受“true”，“false”，“0”和“1”。 

许多设置定义外部数据文件路径。该路径可以是相对路径或绝对路径，但在Unix和Windows环境下目录分隔必须是'/'。 

有些变量包含逗号分隔的值，语法如下： 
Namespace.key =value1，value2，value3,etc. 

用于run-indexed value的语法是： 
Namespace.key = [run1value; run2value; run3value;etc] 
有的值在括号内,不同的运行值用分号隔开。每个值也可以是一个逗号分隔值。 
如需更多run indexing信息，查看“Run indexing”。 

设置文件可以包含注释。注释行必须以”#“字符开头。在读取的时候跳过本行剩余部分。也可以用来注释掉某行配置。 

有些值（当前的方案名和报告名）支持“值 
填充“。使用此功能，您可以通过setting value动态配置方案名。在使用run indexing时非常有用。只要在设定值得前后加上”%“。运行时，占位符”百分号“之间的值由当前配置文件中的设定值代替。具体查看snw_comparison_settings.txt的一个例子。 

如果“default_settings.txt”存在，始终被读取， 
其它的配置文件能设置更多参数来配置或覆盖部分（全部）由前一个文件给出的设定。
这样，你可以定义对所有的仿真的共性配置，然后在特定仿真时，用后面的配置文件覆盖前面的设定，来实行不同的、特殊的仿真。

（如果你的模拟没有任何共同 
参数（这是极不可能的）只是提供一个空的默认 
设置文件。如果你想使用多个默认配置设置，只需 
创建单独的文件夹的所有配置设置，并提供一个默认 
设置文件的每个文件夹。 
）p.s.在1.4.1的readme文件中没有关于这段话的原文。



Run indexing
------------ 

Run indexing为你提供了一种只需要一个配置文件来实行多个不同配置实施的方法。这个想法是：你设定一组运行仿真时变量需要改变配置（使用上述语法）。例如，如果你要运行的模拟使用五个不同的随机数发生器来生成运动模型种子，你可以在配置文件中这样定义： 

MovementModel.rngSeed = [1;2;3;4;5] 

现在，如果你使用下面命令： 

./one.sh - b 5 my_config.txt 

首先使用seed1（运行指数0），然后又运行使用seed2，etc。注意，如果要使用不同的值，你必须在批处理模式下运行它（- b选项）。没有批处理命令，在GUI模式下（多个数）时，只有第一个参数被运行。 

Run indexes wrap around（环绕运行指标）：使用的值是值得（数量）的指数？？（runIndex％arrayLength）。由于环绕（60甲子年），你可以很容易地运行大量的排列。例如，如果定义两个key-value对： 

key1 = [1;2] 
key2 = [a;b;c] 

运行模拟的run-index为6，你会得到key1，key2的所有排列（1,a;2,b;1,c;2,a;1,b;2,c）。对任何数量的数组都适用。只要确保最小的共同所有数组大小为1（例如，使用数组的大小是素数） 
- 除非你不希望所有的排列，但有些值应被配对。 

here！！！！！！！！！！！！！！！




移动模式 
--------------- 

移送模式管理节点在仿真中的移动方式。它提供节点坐标，速度和停留时间。基本安装包含5个动作模式：random waypoint（随机游走）, map based movement（基于地图移动）, shortest path map based movement（基于地图的最短路径移动）, map route movement（基于地图的路线移动？） and external movement
除了external movement，其它移动模式都可以可配置的速度和停留时间的分布。给出一个上限和一个下限，移动模式等概率的这个区间分布中选择一个数值。
同样也适用于暂停时间。在external movement模型速度和停留时间是给定的数据。 

当一个节点使用随机游走模型（RandomWaypoint），它是通过给出仿真区域内的一个随机坐标体，然后以直线匀速移动那坐标，停顿了一会儿，然后产生一个新的目的地。如此整个仿真中节点沿着这些曲折的路线移动。 

在基于地图移动模型中，由预定路径限制节点的运动。可以定义不同种类的路径，对所有的节点组定义有效路径。这样，可以防止汽车开到室内或行人道上。 

基于地图移动模型（MapBasedMovement）开始分配图中两个任意的有路径相连的节点，
然后从一个节点移动到另一节点。当节点到达下一个节点后，从有路径相连的节点中随机选择一个作为下一个目的，只有当除了原起点之外没有其他路径时，就选择原路径返回（避免回走回头路）。当节点移动10-100节点后，停顿了一会儿，然后又开始移动。 

更先进的基于地图移动模型的版本（ShortestPathMapBasedMovement）使用Dijkstra最短路径算法寻地图中到达目的的路径。一旦一个节点到达目的地，等待一个时间，选择一个新的随机节点，以最短路径移动过去，只取图中有效的节点。 

基于地图的最短路径移动模型，地图数据包含兴趣点（POIs）。以一个设定的概率从POI组中选出一个POI作为下一目的代替随机选择。POI组数量可以不限，每个组中的POI节点数目也不限。每个POI组可以分配不同的选择概率。POIs可以用来模拟商店，餐馆和旅游景点。 

基于路线移动模式（MapRouteMovement）可以用来模拟基于设定路线的节点移动，例如公共汽车或电车线路。只需要定义线路上的站点，然后节点以最短路线沿着站点移动，并在每个站点停留一个设定时间。 

所有的运动模型也可以决定何时启动节点活动（移动，可连接），何时停止节点。所有的模式，除了external movement，都可以设置多个仿真时间间隔，节点集只有在那个时间间隔内处于活动状态。
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
所有基于地图的移动模式通过well known text（WKT）格式的一个子格式来输入数据。 WKT的LINESTRING和MULTILINESTRING指令，由对地图路径信息解析器支持。对于点的数据（如POIs），由点指令的支持。相邻的一（多）个节点构成LINESTRING，对于多条线的（某）节点相同时，构成同一条路径，路径在这些点汇集（组成交叉路口）。通过地理信息系统（GIS）程序生成或编辑现实的地图文件生成WKT文件。地图数据包括的模拟器分布的转换和编辑使用免费的，基于Java的 OpenJUMP GIS程序。 


通过存储不同文件包含的不同路径来区分不同的地图类型。兴趣节点在WKT点指令中定义？，将属于同一个组的POIs定义为一个POI group，所有的POI group存放在一个文件中。所有的POIs也必须存在地图数据中，这样它们才能使用的路径（paths）。站点（stops）在LINESTRING中定义，以相同的顺序？访问当它们出现在LINESTRING上。一个WKT文件包含多条路径，它们以相同的顺序出现在文件中。



实验运动模型，它使用外部移动数据（ExternalMovement）按照读取文件中节点的时间戳顺序在仿真中移动节点。在javadocs 的ExternalMovementReader类中查看详细的输入格式。一个相配的，TRANSIMS数据的实验数据转换脚本（transimsParser.pl）包含在 
工具包文件夹中。 

移动模式用来定义“movementModel”设置和每个节点组的联系。该设置的值必须是movement package中的一个有效的运动模型类名。对移动模式通用设定从MovementModel class中读取，对特定的设定分别由单独类中读取。详见javadoc文件和配置示例文件。 

路由模式和信息创建 
------------------------------------ 

路由模式定义消息在模拟中的处理模式。六个不同的路由模式（首次接触First Contact，传染病 Epidemic，喷射和等待Spray and Wait，直接转发Direct delivery， PRoPHET和MaxProp），另外包中还包括一个为external routing simulation的被动路由。现有路由模式都是经典的路由DTN路由算法实现。详见routing package中的类。 

被动路由是专为DTN路由之间的路由仿真或者实施不需要路由功能的仿真而写的。 
路由只在外部事件的命令激励下活动。这些仿真的外部事件由EventQueue的接口类实现。 

目前的版本包括两个消息事件源类：ExternalEventsQueue和MessageEventGenerator。前者可以读取由手工创建的文件、合适的脚本（toolkit文件创建的createCreates.pl）、converting、可识别的dtnsim2输出中的事件（event）。具体格式见input包中的StandardEventsReader类。 MessageEventGenerator是一个简单的消息生成的类，从设定的消息生成间隔、消息大小、源目的主机范围中以均匀分布生成消息。 

工具包文件中包含一个针对DTNsim2输出的实验性的语法分析器脚本（dtnsim2parser.pl） 。（原先有个更强大的基于java的语法解析器，但这个拓展脚本更加简版于是被抛弃了）该脚本需要一些dtnsim2的代码些补丁，补丁在toolkit/dtnsim2patches文件夹。 

路由模块使用的定义每个节点组成立“路由器”。各种路由器不能正常进行交互（例如，PRoPHET路由器只能 
与其他PRoPHET路由协作），所以通常对所有的组使用相同（兼容）的路由。 

报告 
------- 

报告用来生成仿真产生的总结数据，连接和消息的详细数据，使得文件适合进行后期处理，GraphViz（创建图表），实现与其他程序的接口。详见 report-package classes类。 

对于任何的仿真可以生成任意数目的报告（report），报告的数目在"Report.nrofReports"中设置。报告类名称在“Report.reportN”中设置，其中N是从1开始的整数。设定的值为report package中有效的报告类名（report class）。所有的输出报告（用“output”设定可以覆盖掉给个报告类）在Report.reportDir -setting中定义。如果报告类中没有“output”的设定，报告文件的名字就会是“ReportClassName_ScenarioName.txt”。 

所有的报告有很多可配置的设定，由ReportClassName.settingKey -syntax定义。详见Javadocs中的Report class 和 specific report classes（查看“setting ID”的定义）。 

主机组（host group）
----------- 

主机组是是一组有着相同的移动模式和路由模式设定的主机（节点）。不同的组的设定值不同，以此来区分不同的节点类别。在“group”namespace中设定基本的设置，不同的节点组的设定重载（override）这些基本设定，并在特殊namespace（Group1，Group2...）中配置不同的设定。 

设定 
------------ 

有大量的可配置设定，不一一列出。详见javadocs中的report、routing、movement model 类。同样还有配置文件的例子。 

也许最重要的设置如下。 


方案设定： 

Scenario.name 
方案名。所有的报告文件是有这个默认前缀。 

Scenario.simulateConnections 
连接模拟使能。如果你只关心移动模式，关闭此项可以是仿真速度加快。通常会开启此项。 

Scenario.updateInterval 
设定更新周期。提高此项可以加快仿真，但会失去一些精度。0.1~2是不错的选择。 

Scenario.endTime 
模拟时间，单位s（seconds）。 

Scenario.nrofHostGroups 
仿真中的主机组数。 

Interface settings（定义节点可能的接口）
--- 

type 
接口使用的类（在interfaces-directory中）

剩下的设定为class-specific（指定类），可定义如下：

transmitRange
接口中传播范围（米）？？？？

transmitSpeed
接口中的传播速度，单位bps（bytes per second）



主机组设定（在group或GroupN namespace中使用）。
---

groupID 
组的标示符，字符串类型或字符型（string、char）
在报告和GUI中作为主机名的前缀。主机的全名为groupID + NetworkAddress。 

nrofHosts 
组中的主机数。 

nrofInterfaces
组中节点使用的接口数

interfaceX
接口数量为X

movementModel
组中所有主机的移动模式。必须为movement package中的一个有效类（movementmodel class中的一个子类）。

waitTime
最大和最小的等待时间范围（由逗号分开的两个值组成），单位s（second）。min，max。定义了节点在路径中到达当前目的位置后停留的时间。在每次到达后在区间中随机产生一个值。缺省为0,0.

speed
定义节点移动的最大和最小速度范围（由逗号分开的两个值组成），单位m/s.在每段路径上，随机产生一个区间内的值作为速度。缺省为1,1.

bufferSize
节点的消息缓冲区大小，单位bytes。缓冲区满后，节点将无法接收新的消息，除非删除缓冲区中旧的消息


router
仿真中发送消息的路由机制，必须为routing package中的一个有效类名（report class的子类）


activeTimes 
组中节点启动的时间区间（用逗号分开的两两时间值组：start
，end1，start2，end2....）。没有进行定义时，节点始终处于活跃状态。 

msgTtl 
主机组生成的消息的生存时间（仿真时钟的分钟）。 
活动的节点以分钟为周期，检验消息的ttl是否归0，并丢弃此类消息。如果没有对此项设定，消息将一直存在。 

=====================================================
特殊组和运动模式设置（仅适用于某些有意义运动模式）： 

pois
兴趣点的指标（indexes）和概率（probabilities）（逗号分隔指数-概率元组：poiIndex1，poiProb1，poiIndex2，poiProb2，... ）。 
指标为整数，概率为0.0~1.0之间的小数。 
设置定义了主机组中的节点选择POI组哪个作为（下一跳）目的地和选择一个POI组的概率。例如，一个（随机）的POI定义在POI file1（在PointsOfInterest.poiFile1中定义）。
如果所有的概率和小于1，选择任意的随机节点作为下一目的地（1.0-theSumOfProbabilities）。设置只能用于ShortestPathMapBasedMovement为基础的运动模型。 

okMaps 
哪些地图节点类型（根据地图文件指标定义）对这个组ok（适合)（逗号分隔的整数列）。节点移动只会通过OK的节点。默认情况下，所有地图节点都ok。设置可用于任何MapBasedMovent的基础运动模型。 

routeFile 
如果使用了MapRouteMovement运动模型，就定义了路由读取route file的路径。路由文件包括LINESTRING WKT指令。 每个vertex代表LINESTRING中的一个站点。 

routeType 
如果MapRouteMovement运动模型，则此设置定义的路线 
类型。类型可以是圆形（值1）或乒乓（值2）。见 
movement.map.MapRoute类的细节。 


运动模式设置： 

MovementModel.rngSeed 
所有的运动模型的随机数发生器的种子。如果种子和 
所有的运动模型相关的设置保持不变，所有节点在不同的仿真中以相同的模式移动（相同的目的地，速度和停留时间）。 

MovementModel.worldSize 
仿真世界的大小，单位米（meters）（逗号分开的二值组：宽，高）。 

PointsOfInterest.poiFileN 
==========================
对于ShortestPathMapBasedMovement为基础的运动模型，此设置定义 
西九龙总站文件，其中的POI读取坐标。 POI的坐标 
西九龙总站的点来定义指令。的“N”中的设置必须结束 
是一个正整数（即poiFile1，poiFile2，...). 

MapBasedMovement.nrofMapFiles 
多少看地图文件设置在设置文件。 

MapBasedMovement.mapFileN 
第N个地图文件的路径文件（“N”的必须是一个正整数）。在配置文件中至少要定义有 nrofMapFiles的分离文件。所有地图文件必须是有着LINESTRING和/或MULTILINESTRING指令的WKT文件。 
地图文件可以包含POINT指令，但这些都是被跳过的。这样，同一个（多个）文件可同时用于POI和地图数据。通过默认地图坐标转换，使地图左上角的点为坐标系的（0,0）。 Y轴坐标在转换前反射然后翻译，以便该地图的北点在操场视图上？？？？。所有的POI和路由文件都要转换来对应地图数据变换。


报告设定： 

Report.nrofReports 
要加载多少报告模块。模块名称定义在设置“Report.report1”，“Report.report2”等。如下的报告配置可以同时对所有的报告统一设定（使用report namespace），也能单独设定某些报告（使用ReportN N为第几个报告）。 

Report.reportDir 
报告输出文件的存放位置。可以仿真开始后的绝对路径或相对路径。如果目录不存在，创建一个。 

Report.warmup 
预热时间（从仿真开始计算，单位sencond）。在预热时间中，报告文件不保存新事件。该行为是报告模块的特性，详见各个java文档中的不同的报告模块。 


事件生成器设置： 

Events.nrof 
仿真需要加载的事件生成器数量。事件产生器（见下文）中定义的特殊设定在EventsN命名空间中（所以Events1.settingName配置了第一个事件生成器，等）。 

EventsN.class 
要加载的生成器的类名称（例如，ExternalEventsQueue或MessageEventGenerator）。这个类必需能从输入包找到。 

对于ExternalEventsQueue您必须至少定义路径到外部 
事件的文件（使用设置“filePath的”）。见input.StandardEventsReader类' 
Javadoc中有关不同的外部事件的信息。 


其他设置： 

Optimization.randomizeUpdateOrder 
节点上传的顺序是随机的。 
请求上传使节点检查他们的连接性并且上传路由模块。如果设置为false，节点以网络地址的顺序上传。在随机情况，每次上传的顺序是不同的。 



图形用户界面(GUI)
=== 

图形用户界面的主窗口分为三个部分。主体部分包含场景（playfield）视图（显示其中节点运动）、仿真和GUI控制和信息。右边部分是用来选择节点、下部用来记录和设置断点。 

主体部分的顶层部分是用于仿真和图形用户界面控制。该 
第一个窗口显示当前的模拟时间。下一个位置显示模拟速度（仿真的每秒/s）。接着4个按钮分别是：暂停/开始、单步、开启/结束快速仿真、以给定的时间进行快速仿真。用单步按钮可以以一定的时间间隔观看模拟的每一步。快进（FFW）可以用来跳过不感兴趣的仿真部分。在FFW模式下，GUI以较大的数值更新速度。接着的一个下拉式菜单是用来设置GUI 更新速度。速度1意味着GUI是仿真更新速度为每秒一次。速度10的GUI的更新为每10秒一次。负值可以降低仿真速度。接着一个控件用来调整窗口大小。最后一个用来截图。

中间部分，场景视图，显示了节点的位置，地图路径，节点标识，节点之间的连接等 。
每个节点用蓝色小方格表示，以绿色圆圈表示此节点的通讯范围。节点的组标识符和网络地址（一个数字）显示在每个节点的旁边。如果一个节点携带了消息，每个消息是一个绿色或蓝色实心矩形，落起来的柱状（看不到就放大）。如果节点携带超过10条信息，每10个消息绘成一个矩形，颜色为红或蓝（代表10位）。调节边缘的滑块移动视图，用鼠标滑轮调整视图分辨率。 

在主窗口的右边部分是选择一个节点查看详情。 
只需点击一个按钮，在主窗口下方显示节点的信息。 
点击后面的下拉式菜单，选取某个节点所携带的信息，可以查看信息详情。点击 
“routing info”打开一个新窗口，显示路由模式信息。当选中一个节点，主视图显示当前节点，并用红线描述当前到这个节点的移动路径。 

日志记录（最低部分）分为两部分，事件记录控制和事件记录。在控制部分，你可以选择记录中所要显示的部分。还可以定义模拟中对某些类型的事件需要暂停（使用“暂停”列中的复选框）。事件日志中显示事件的时间戳。左右的节点和消息名都是按钮，可以通过直接点击查看详情。 

DTN2 Reference Implementation Connectivity
========================================== 

DTN2连接允许ONE绑定（bundle）多个DTN2路由器。有DTN2的外部汇聚层接口实现。 

当DTN2连接启用时，ONE将连接dtnd 路由作为外部汇聚层适配器。ONE通过绑定一个链接和路由到仿真，实现连接控制自动配置dtnd，

当从dtnd收到一个绑定，ONE尝试将目标的EID和在配置文件中配置的正则表达式进行匹配（见DTN2连接配置文件如下）。对于每个匹配的节点生成一个消息副本并在ONE内部路由。当绑定到达ONE里的目的地，。 



采取以下步骤实现此功能功能： 

1）DTN2必须被编译和设置ECL支持启用。 
2）DTN2Events事件产生器必须配置为一个事件类被载入。 
3）DTN2Reporter必须配置为报告类加载。 
4）DTN2连接配置文件必须被配置为DTN2.configFile 

启动模拟： 
1）启动所有dtnd路由器实例。 
2）启动ONE。 

配置示例（2-4） 
--------------------------------- 

Events.nrof = 1 
Events1.class = DTN2Events 
Report.nrofReports = 1 
Report.report1 = DTN2Reporter 
DTN2.configFile = cla.conf 

DTN2连接配置文件 
------------------------------------ 

该DTN2连接配置文件定义ONE中的那些节点应该连接哪些DTN2路由器实例。它还定义节点对应EID的配对。 

配置文件由＃开始的注释和具有以下格式的配置行： 

<nodeID> <EID regexp> <dtnd主机> <ECL端口> <console端口> 

这些字段的含义如下： 

nodeID：ONE中一节点ID（整数> = 0） 

EID regexp：传入的绑定的目的地EID和正则表达式匹配，将被转发到ONE内部的节点。 
（见java.util.regex.Pattern） 

dtnd host：连接到dtnd路由器的主机名/ IP。 

ECL port：dtnd路由器的ECLAs监听端口 

console port：dtnd路由器的控制端口 

例如： 
＃<nodeID> <EID regexp> <dtnd主机> <ECL端口> <console端口> 
1 DTN://本地1.dtn/(.*) localhost 8801 5051 
2 DTN://本地2.dtn/(.*) localhost 8802 5052 

已知问题 
------------ 

对于DTN2连接相关的问题，您可以联系teemuk@netlab.tkk.fi 

-Quitting dtnd router instances connected to ONE会导致ONE停止退出。 

工具包 
======= 

仿真包包括一个名为“工具包”的文件夹，其中包含生成的输入和处理模拟器的输出的脚本。所有（目前包括）脚本是用Perl编写的（http://www.perl.com/），因此你需要在运行脚本之前安装perl。一些处理使用gnuplot脚本（http://www.gnuplot.info/）创建图形。两者都提供免费的流行的Unix / Linux和Windows环境下的程序。对于Windows环境中，可能需要更改脚本的可执行路径。 

getStats.pl 
此脚本可用于对MessageStatsReport -report创建各种统计模块。唯一的强制选项是“- Stat”，用来定义需要从报告文件中解析的统计值的名称（例如，“delivery_prob”为消息传播的概率）。其余的参数为MessageStatsReport输出文件名（或路径）。脚本创建了三个输出文件：一个关于所有文件的值，一个由使用gnuplot命令创建的图形，最终形成的图像文件。一个为解析每个输入文件而创建的bar。每个bar的标题是用报告名的正则表达式解析而来，这个包文件名定义为“-label”选项。运行getStats.pl中的“-help”查看更多信息。 

ccdfPlotter.pl 
用从报告中来创建互补（/逆）累积分布函数图（使用gluplot）的脚本，包含time-hitcount-tuples。输出文件名必须定义为”-out”选项，其他参数应该为（适当）的报告文件名。 “标签”选项可用于定义legend标签的提取正则表达式（类似于getStats 
script）。 

createCreates.pl 
仿真中的消息声模式可以有外部事件文件定义。这样的文件只需要简单文本编辑器就能创建，但这个脚本可以更容易地创造数量很大的消息。强制性选项的为：消息的数量（“- nrof”），时间范围（“-time”），主机地址范围（“-hosts”）和消息大小范围（“-size”）。
该邮件的数量仅仅是一个整数的范围，由一个冒号（:）分开的两个整数表示。如果主机要对收到的消息进行应答，用“-rsizes”来定义应答消息的大小。如果需要使用一个确定的随机数生成器的种子，可以用"-seed"选项定义。
所有的随机数以相同的概率从半开半闭区间中取值，这个区间一个包括最小值和不包括最大值.脚本输出命令适用外部事件文件的内容。你可能想要对部分输出文件重定向。

dtnsim2parser.pl和transimsParser.pl 
这两个（相当实验）分析器将其他程序中的数据转换的适合ONE。都有两个参数：输入和输出文件。如果这些参数被省略，stdin和stdout（标准输入输出）用于输入和输出。“- h”选项，给出一些简明的帮助信息。 



dtnsim2parser转换dtnsim2的（http://watwire.uwaterloo.ca/DTN/sim/）输出 
（冗余模式8）外部事件文件，可以适用ONE。该这个解析器的主要思想是，你可以先用ONE和ConnectivityDtnsim2Report建立一个连接模式的文件，然后到给dtnsim2，在ONE中看到可视化结果（以dtnsim2parser作为外部事件文件，来转化输出）。 

transimsParser将TRANSIM的（http://transims-opensource.net/）车辆 
快照文件作为外部移动文件转换为节点移动的输入文件。详见ExternalMovement和ExternalMovementReader类。

   :)
