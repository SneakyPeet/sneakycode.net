{:tags ("chatops" "devops" "hubot"), :title "Getting Started With Chat Ops", :date "2015-02-24"}
-----
I recently gave a talk about ChatOps at [Entelect's Dev Day](http://entelect.co.za). The talk went really well and after some discussion with attendees I thought it might be useful to write  a post about how to get started with chat ops.

___
**So What is ChatOps?**

To understand ChatOps we first need to talk about DevOps. DevOps is about knocking down barriers between developers and operations and in doing so reducing orginizational friction. Source control, continuous integration and automated deploy all form part of DevOps. DevOps helps us go fast while still going clean. At the heart of DevOps is CAMS. To move fast and maintain stability, it is important to have a **culture** of **automation**, **measurement** and **sharing**. ChatOps is an extension of DevOps and enhances it with and extreme focus on CAMS. 

**What are the Benefits?**

ChatOps moves all your everyday tasks into one place, where everyone can see it. Your tools become part of your conversation. This provides you with a history of events. No longer do you have to ask questions like "Is the build green" or "are you finished deploying". Just have a look in your chat room.

It creates a passive learning environment for new team members. They see how everything is done just by participating in the chat. It is like pair programming but for DevOps. You teach them by just doing your daily tasks.

It hides your ugly. No longer do you have to manage access to all the tools that you use. You don't have to remember urls and passwords. You provide a single access point for everyone, inside your chat room.

It improves remoting. You can easily manage tasks using the chat app on your phone, from anywhere. Want to check site load on your commute? Now you can easily do that and other tasks from your chat room.

Lastly it improves speed and repeatability. When you automate tasks and provide simple access to all your tasks in one place, speed increases and human error likelihood decreases.

**How do I get started with ChatOps?**

Well, first of all you will need a chat solution that provides **inline imaging** and **has an API**. There are many good ones out there, but a good start might be [Hipchat](http://hipchat.com) or [Slack](https://slack.com/). 

Next, get yourself a chat robot. This robot is your chat room butler. You ask him to do various things to make your life fun and easy. There are a few bots out there, written in various languages, the most used being [Hubot](https://hubot.github.com/), [Lita](https://www.lita.io/) and [Err](http://errbot.net/).

![hubot](http://res.cloudinary.com/dltpyggxx/image/upload/v1424792361/hubot2_spk5mw.jpg)

The next, **most important** step, is to start building a **culture** of chat ops in your team. Regardless of how amazing you make your automation tools, if the culture is non-existent, you are wasting your time. Here is how you start building that culture:

* Create a chat room for your team/project. Start moving communication into this chat room and away from email. Make sure everyone on your team is online when they are at work and if they are remote, teach them to announce when they come and go. If your team is remote you can start moving daily standup into chat as well.
*  Next start using your chat robot to add some fun in your chat room. Give it the ability to fetch random images or videos for you. Have it make memes and answer eight ball questions. There are many fun scripts out there to help you get started with The Fun. 
*  Create fun chat room based games for your team and play them on a regular basis. On our team we have crap song Friday, where everyone needs to paste a video of a song that is just bad. We also play WTF roulette where everyone ask Hubot to get a WTF image until we get something really bad and then we quickly hide it with a [pug bomb](https://www.npmjs.com/package/hubot-pugme).

Ok I can hear you saying that this just generates a lot of noise. And you are right, your team can run the risk of losing valid information due to noise generated, but again this comes down to team culture. Make simple rules about "noise". We easily solved this by having one general chat room and separate chatrooms for specific projects. In the general chat anything goes and you move important content to relevant chat rooms. Also do not be afraid of change. If something just does not work, chuck it and try something else. Each person and team is unique, so figure out what works for you.

![Aliens Guy ChatOps](http://res.cloudinary.com/dltpyggxx/image/upload/v1424792359/Capsdfdsfdsfture_s7w2br.png)
The next step is to start making it **informative**. Start posting notifications into the room using the chat app's api, web hooks and tools like IFTTT and Zapier. Did a CI build fail? Post that into the chat. Did someone push to master? Post that into the chat. Has a ticket been created on the backlog? Did someone mention your product on twitter? Was there an application exception? Post it to your chat room. Whenever you find yourself having to go check if an event occured, move that into your chat room.

Lastly make it **productive**. Identify simple tasks that you can automate. Get your chat robot to start and stop CI builds, create trello tickets and draw yourself a graph of the server load over the last 24 hours. Start using the Api's of the tools that you rely on. Once you have automated these simple tasks, go on and start automating the difficult ones. Have your chat bot do a production deploy or setup everything that you need when a new employee joins your team.

![Hubot build team city build](http://res.cloudinary.com/dltpyggxx/image/upload/v1424792383/build_ov1kau.png)

___
ChatOps has the potential to provide a ton of value. Better communication, engaged team members, more speed, reliability and overall fun. However do not expect buy-in from everyone from the get go. The success of your ChatOps endeavour hinges on the culture that you create. Spend your time here. It took my team about a year to build culture and we are only now starting to make it the chatroom productive. 
Realise the potential of Api's. Anything that exposes an api can potentially be used by your chat robot. Anything with a web hook can push info to you. I will dare say that anything will become possible.

Good luck and happy chats.

<a href="http://www.codeproject.com/script/Articles/BlogFeedList.aspx?amid=8804440" rel="tag" style="display:none">CodeProject</a>
