package org.tju.so.model.notification;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Notification {

    public enum Receiver {
        ALL, CRAWLER_SCHEDULER, CRAWLER_WORKER, DISPATCHER, SEARCH_NODE
    }

    public enum Topic {
        RELOAD, PUSH
    }

    private Receiver receiver;

    private Topic topic;

    private Object argument;

    public Notification() {}

    public Notification(Receiver receiver, Topic topic, Object argument) {
        setReceiver(receiver);
        setTopic(topic);
        setArgument(argument);
    }

    public boolean isFeedingReceiver(Receiver target) {
        if (receiver == Receiver.ALL)
            return true;
        if (receiver == target)
            return true;
        return false;
    }

    /**
     * @return the receiver
     */
    public Receiver getReceiver() {
        return receiver;
    }

    /**
     * @param receiver
     *            the receiver to set
     */
    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    /**
     * @return the topic
     */
    public Topic getTopic() {
        return topic;
    }

    /**
     * @param topic
     *            the topic to set
     */
    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    /**
     * @return the argument
     */
    public Object getArgument() {
        return argument;
    }

    /**
     * @param argument
     *            the argument to set
     */
    public void setArgument(Object argument) {
        this.argument = argument;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Notification [receiver=" + receiver + ", topic=" + topic
                + ", argument=" + argument + "]";
    }

}
