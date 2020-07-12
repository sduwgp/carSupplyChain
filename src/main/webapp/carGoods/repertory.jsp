<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>第一个 ECharts 实例</title>
    <!-- 引入 echarts.js -->
    <script src="https://cdn.staticfile.org/echarts/4.3.0/echarts.min.js"></script>
    <script src="../js/jquery-1.8.2.min.js"></script>
</head>
<body>
<!-- 为ECharts准备一个具备大小（宽高）的Dom -->
<div id="main" style="width: 600px;height:400px;"></div>

<div id="main1" style="width: 600px;height:400px;"></div>
<script type="text/javascript">
    // 基于准备好的dom，初始化echarts实例
    var myChart = echarts.init(document.getElementById('main'));

    //通过Ajax获取数据
    $.ajax({
        type : "post",
        async : true, //异步执行
        url : "../cargoods/getCarGoodsSale",
        dataType : "json", //返回数据形式为json
        success : function(data) {
            console.log(data);
            if (data) {
                // 指定图表的配置项和数据
                myChart.setOption({
                    title: {
                        text: '第一个 ECharts 实例'
                    },
                    tooltip: {},
                    legend: {
                        data:['销量']
                    },
                    xAxis: {
                        data: data.categories
                    },
                    yAxis: {},
                    series: [{
                        name: '销量',
                        type: 'bar',
                        data: data.data
                    }]
                });
            }
        },
        error : function(errorMsg) {
            alert("请求数据失败");
        }
    });

    var myPieChart = echarts.init(document.getElementById('main1'));
    // option = {
    //     title : {
    //         text: '某站点用户访问来源',       //大标题
    //         subtext: '纯属虚构',                //类似于副标题
    //         x:'center'                 //标题位置   居中
    //     },
    //     tooltip : {
    //         trigger: 'item',           //数据项图形触发，主要在散点图，饼图等无类目轴的图表中使用。
    //         formatter: "{a} <br/>{b} : {c} ({d}%)"   //{a}（系列名称），{b}（数据项名称），{c}（数值）, {d}（百分比）用于鼠标悬浮时对应的显示格式和内容
    //     },
    //     legend: {                           //图例组件。
    //         orient: 'vertical',             //图例列表的布局朝向
    //         left: 'left',
    //         data: ['直接访问','邮件营销','联盟广告','视频广告','搜索引擎']
    //     },
    //     series : [              //系列列表。每个系列通过 type 决定自己的图表类型
    //         {
    //             name: '访问来源',
    //             type: 'pie',
    //             radius : '55%',
    //             center: ['50%', '60%'],
    //             data:[
    //                 {value:335, name:'直接访问'},
    //                 {value:310, name:'邮件营销'},
    //                 {value:234, name:'联盟广告'},
    //                 {value:135, name:'视频广告'},
    //                 {value:1548, name:'搜索引擎'}
    //             ],
    //             itemStyle: {
    //                 emphasis: {
    //                     shadowBlur: 10,
    //                     shadowOffsetX: 0,
    //                     shadowColor: 'rgba(0, 0, 0, 0.5)'
    //                 }
    //             }
    //         }
    //     ]
    // };
    // myChart.setOption(option);

    $.ajax({
        type : "post",
        async : true, //异步执行
        url : "../cargoods/getCarGoodsSaleByPie",
        dataType : "json", //返回数据形式为json
        success : function(data) {
            console.log(data);
            if (data) {
                // 指定图表的配置项和数据
                myPieChart.setOption({
                    title: {
                        text: '商品销售统计',       //大标题
                        subtext: '纯属虚构',                //类似于副标题
                        x: 'center'                 //标题位置   居中
                    },
                    tooltip: {
                        trigger: 'item',           //数据项图形触发，主要在散点图，饼图等无类目轴的图表中使用。
                        formatter: "{a} <br/>{b} : {c} ({d}%)"   //{a}（系列名称），{b}（数据项名称），{c}（数值）, {d}（百分比）用于鼠标悬浮时对应的显示格式和内容
                    },
                    legend: {                           //图例组件。
                        orient: 'vertical',             //图例列表的布局朝向
                        left: 'left',
                        data: data.categories
                    },
                    series: [              //系列列表。每个系列通过 type 决定自己的图表类型
                        {
                            name: '销售数量',
                            type: 'pie',
                            radius: '55%',
                            center: ['50%', '60%'],
                            data: data.data,
                            itemStyle: {
                                emphasis: {
                                    shadowBlur: 10,
                                    shadowOffsetX: 0,
                                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                                }
                            }
                        }
                    ]
                });
            }
        },
        error : function(errorMsg) {
            alert("请求数据失败");
        }
    });
</script>
</body>
</html>