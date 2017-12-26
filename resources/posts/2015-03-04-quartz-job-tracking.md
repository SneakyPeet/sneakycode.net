{:tags ("csharp" "quartz"), :title "Job Tracking using Quartz for .net", :date "2015-03-04"}
-----
A project that I recently worked on has a worker role that runs about 40 jobs. With some of these jobs running for hours it became imperative that we knew when these jobs were running, if they were currently running, how long they were running, if they were overlapping with other dependent jobs etc.  

Below I will show you how to easily setup Job Tracking using [quartz-scheduler](http://www.quartz-scheduler.net/) for .net.

**Listeners**

Quartz.net provides us with the concept of Listeners. Listeners are objects that you create to perform actions based on events occurring within the scheduler. Quartz provides the following three listener interfaces:

* ITriggerListener
* IJobListener
* ISchedulerListener 

Each of these provides you with methods that get called on various events. More info on these can be found [here](http://www.quartz-scheduler.net/documentation/quartz-2.x/tutorial/trigger-and-job-listeners.html) and [here](http://www.quartz-scheduler.net/documentation/quartz-2.x/tutorial/scheduler-listeners.html).

**Setup**

First you need to create the implementation of whatever listener you want.
<pre><code class="language-csharp">public class MyJobListener : IJobListener</code></pre>
Then you need to register the listener with your scheduler.
<pre><code class="language-csharp">var scheduler = SchedulerFactory.GetScheduler();
scheduler.ListenerManager.AddJobListener(myInstanceOfJobListener);
scheduler.ListenerManager.AddSchedulerListener(myInstanceOfScheduleListener);</code></pre>
All that is left is to implement the methods for the events you want to hook into.
You can register multiple listeners of the same type.

**Implementation**

For my solution I hooked into the following methods
On IScheduleListener:
<pre><code class="language-csharp">public void JobScheduled(ITrigger trigger)
{
    var name = trigger.JobKey.Name;
    DateTime? nextStartDate = null;
    if (trigger.GetNextFireTimeUtc().HasValue)
    {
        nextStartDate = trigger.GetNextFireTimeUtc().Value.DateTime;
    }
    //Do something with the information
}</code></pre>
This method gets called each time a job is scheduled. I use this so keep track of all the jobs that are scheduled and can easily extract which jobs have never ran. If you need to you can also extract the trigger type. You can then use the trigger type to form some kind of calendar view of when jobs will run.

On IJobListener:
<pre><code class="language-csharp">public void JobToBeExecuted(IJobExecutionContext context)
{
    var name = context.JobDetail.JobType.Name;
    DateTime startTime = context.FireTimeUtc.HasValue ? context.FireTimeUtc.Value.DateTime : DateTime.UtcNow;
    //do something with info
}

public void JobWasExecuted(IJobExecutionContext context, JobExecutionException jobException)
{
    var name = context.JobDetail.JobType.Name;
    var runtime = context.JobRunTime;
    if(jobException != null)
    {
        //do something with error info
    }

    DateTime? nextRunDate = null;
    if(context.NextFireTimeUtc.HasValue)
    {
        nextRunDate = context.NextFireTimeUtc.Value.DateTime;
    }
    //do something with info
}</code></pre>
It is apparent that these methods get called just before a job executes and just after it finished. JobWasExecuted will get called even if there was an exception and quartz is nice enough to provide you with the exception object.

As mentioned we use the data gathered to make sure our worker role is in good health. There are various other methods on these interfaces so have a look at the linked documentation.
___
*This post was first published on [blog.entelect.co.za](http://blog.entelect.co.za/home)*

<a href="http://www.codeproject.com/script/Articles/BlogFeedList.aspx?amid=8804440" rel="tag" style="display:none">CodeProject</a>
