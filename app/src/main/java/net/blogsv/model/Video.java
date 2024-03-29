package net.blogsv.model;

/**
 * Created by Tamim on 08/10/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Video {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("downloads")
    @Expose
    private Integer downloads;
    @SerializedName("review")
    @Expose
    private Boolean review = false ;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("extension")
    @Expose
    private String extension;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("image")
    @Expose
    private String image;


    @SerializedName("video")
    @Expose
    private String video;



    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("userid")
    @Expose
    private Integer userid;
    @SerializedName("userimage")
    @Expose
    private String userimage;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("tags")
    @Expose
    private String tags;
    @SerializedName("comments")
    @Expose
    private Integer comments;
    @SerializedName("comment")
    @Expose
    private Boolean comment;

    @SerializedName("like")
    @Expose
    private Integer like;
    @SerializedName("love")
    @Expose
    private Integer love;

    @SerializedName("angry")
    @Expose
    private Integer angry;

    @SerializedName("haha")
    @Expose
    private Integer haha;

    @SerializedName("sad")
    @Expose
    private Integer sad;

    @SerializedName("woow")
    @Expose
    private Integer woow;



    private boolean downloading = false;
    private int progress;
    private String path;


    private String local;

    public Boolean getComment() {
        return comment;
    }

    public void setComment(Boolean comment) {
        this.comment = comment;
    }
    private int viewType = 2;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }



    public Boolean getReview() {
        return review;
    }

    public void setReview(Boolean review) {
        this.review = review;
    }



    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }



    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }


    public int getViewType() {
        return viewType;
    }

    public Video setViewType(int viewType) {
        this.viewType = viewType;
        return this;
    }


    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public void setLike(Integer like) {
        this.like = like;
    }

    public void setLove(Integer love) {
        this.love = love;
    }

    public void setAngry(Integer angry) {
        this.angry = angry;
    }

    public void setHaha(Integer haha) {
        this.haha = haha;
    }

    public void setSad(Integer sad) {
        this.sad = sad;
    }

    public void setWoow(Integer woow) {
        this.woow = woow;
    }

    public Integer getLike() {
        return like;
    }

    public Integer getLove() {
        return love;
    }

    public Integer getAngry() {
        return angry;
    }

    public Integer getHaha() {
        return haha;
    }

    public Integer getSad() {
        return sad;
    }

    public Integer getWoow() {
        return woow;
    }


    public String getTitle() {
        return title;
    }

    public Integer getDownloads() {
        return downloads;
    }

    public String getType() {
        return this.type;
    }

    public String getExtension() {
        return this.extension;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getImage() {
        return image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDownloads(Integer downloads) {
        this.downloads = downloads;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public boolean isDownloading() {
        return downloading;
    }

    public int getProgress() {
        return progress;
    }

    public void setDownloading(boolean downloading) {
        this.downloading = downloading;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getVideo() {
        return this.video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getLocal() {
        return local;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}