package com.studentadvisor.noam.studentadvisor.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.studentadvisor.noam.studentadvisor.logging.L;
import static com.studentadvisor.noam.studentadvisor.extras.Keys.EndpointsBundles.PARCEL_POST_IN_EXTRA_DEGREE;
/**
 * Created by Noam on 11/28/2015.
 */
public class ExtraInfoDegree implements Parcelable {
    public static final Parcelable.Creator<ExtraInfoDegree> CREATOR
            = new Parcelable.Creator<ExtraInfoDegree>() {
        public ExtraInfoDegree createFromParcel(Parcel in) {
            L.m("create from parcel :ExtraInfoDegree");
            return new ExtraInfoDegree(in);
        }

        public ExtraInfoDegree[] newArray(int size) {
            return new ExtraInfoDegree[size];
        }
    };

    private int numFollowes;
    private int numRated;
    private int numPosts;
    private int numLikes;
    private boolean ifLiked = false;
    private boolean ifPostRate = false;
    private boolean ifFollowed = false;
    private Post post ;
    private Post myUserPost;
    private float scoreRate;
//    private boolean ifFollow


    public ExtraInfoDegree() {

    }

    public ExtraInfoDegree(Parcel input) {
        numFollowes = input.readInt();
        numRated = input.readInt();
        numPosts = input.readInt();
        numLikes = input.readInt();
        ifLiked = input.readByte() != 0;
        ifPostRate = input.readByte() != 0;
        ifFollowed = input.readByte() != 0;
        post = input.readParcelable(Post.class.getClassLoader());
        myUserPost = input.readParcelable(Post.class.getClassLoader());
        scoreRate = input.readFloat();


    }

    public ExtraInfoDegree(int numFollowes,
            int numRated,
             int numPosts,
             int numLikes,
             boolean ifLiked,
             boolean ifPostRate,
             boolean ifFollowed,
             Post post,
            Post myUserPost,
           float scoreRate) {
        this.numFollowes = numFollowes;
        this.numRated = numRated;
        this.numPosts = numPosts;
        this.numLikes = numLikes;
        this.ifLiked = ifLiked;
        this.ifPostRate = ifPostRate;
        this.ifFollowed = ifFollowed;
        this.post = post;
        this.myUserPost = myUserPost;
        this.scoreRate = scoreRate;
    }

    public float getScoreRate() {
        return scoreRate;
    }

    public void setScoreRate(float scoreRate) {
        this.scoreRate = scoreRate;
    }

    public boolean isIfFollowed() {
        return ifFollowed;
    }

    public void setIfFollowed(boolean ifFollowed) {
        this.ifFollowed = ifFollowed;
    }

    public int getNumFollowes() {
        return numFollowes;
    }

    public void setNumFollowes(int numFollowes) {
        this.numFollowes = numFollowes;
    }

    public Post getMyUserPost() {
        return myUserPost;
    }

    public void setMyUserPost(Post myUserPost) {
        this.myUserPost = myUserPost;
    }

    public int getNumRated() {
        return numRated;
    }

    public void setNumRated(int numRated) {
        this.numRated = numRated;
    }

    public int getNumPosts() {
        return numPosts;
    }

    public void setNumPosts(int numPosts) {
        this.numPosts = numPosts;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public static Creator<ExtraInfoDegree> getCREATOR() {
        return CREATOR;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public boolean isIfLiked() {
        return ifLiked;
    }

    public void setIfLiked(boolean ifLiked) {
        this.ifLiked = ifLiked;
    }

    public boolean isIfPostRate() {
        return ifPostRate;
    }

    public void setIfPostRate(boolean ifPostRate) {
        this.ifPostRate = ifPostRate;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public String toString() {
        return "numFollows " + numFollowes +
                "\nnumRated: " + numRated +
                "\nnumPosts " + numPosts +
                "\nnumLikes " + numLikes +
                "\nifLiked  " + ifLiked  +
                "\nifPostRate " + ifPostRate +
                "\nifFollow " + ifFollowed +
                "\npost " + post.toString() +
                "\nmyUserPost " + myUserPost.toString() +
                "\nscoreRate " + scoreRate +
                "\n";
    }

    @Override
    public int describeContents() {
//        L.m("describe Contents Movie");
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(numFollowes);
        dest.writeInt(numRated);
        dest.writeInt(numPosts);
        dest.writeInt(numLikes);
        dest.writeByte((byte) (ifLiked ? 1 : 0));
        dest.writeByte((byte) (ifPostRate ? 1 : 0));
        dest.writeByte((byte) (ifFollowed ? 1 : 0));
        dest.writeParcelable(post, flags);
        dest.writeParcelable(myUserPost, flags);
        dest.writeFloat(scoreRate);
    }
}
