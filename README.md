#简易Android客户端-redesign

link:[http://fir.im/Sinyuk](http://fir.im/Sinyuk "下载地址")

---

 [之前那个](https://github.com/80998062/jianyi)的代码太乱了...看了只有推翻重写的冲动

所以重新设计了一下代码和界面(其实当时是为了参加华为的一个创业比赛)

##截图

![image](https://github.com/80998062/jianyi2/raw/master/pic/pic1.jpg)

![image](https://github.com/80998062/jianyi2/raw/master/pic/pic2.jpg)

主要的改动有:

- 放弃了MVP (感觉对这么简单的东西用处不大...反而多了一大堆代码

- 改了很多界面和动画 (感觉比之前的好多了


- 使用了Rxjava+Dagger2 (依赖注入真的好方便

- 把drawable全部换成了svg (减小了apk体积不说 关键是avd动画很酷炫)
