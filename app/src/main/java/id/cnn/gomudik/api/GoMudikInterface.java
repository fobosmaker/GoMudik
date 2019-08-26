package id.cnn.gomudik.api;

import id.cnn.gomudik.gomudik_ads.model.GetAds;
import id.cnn.gomudik.gomudik_main_package.model.CCTVActivityModel;
import id.cnn.gomudik.gomudik_main_package.model.MainActivityModel;
import id.cnn.gomudik.gomudik_main_package.model.NewsActivityModel;
import id.cnn.gomudik.gomudik_main_package.model.StatusActivityModel;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListChatGroup;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListContact;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListNotification;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListStatus;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListStatusComment;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListUsers;
import id.cnn.gomudik.gomudik_user_and_group_package.model.Login;
import id.cnn.gomudik.gomudik_main_package.model.GetCategories;
import id.cnn.gomudik.gomudik_main_package.model.GetLocationByGroup;
import id.cnn.gomudik.gomudik_main_package.model.GetNearby;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface GoMudikInterface {
    @GET("auth/getCategory")
    Call<GetCategories> getCategories();
    @FormUrlEncoded
    @POST("auth/getMenuData")
    Call<MainActivityModel> getDataMainActivity(@Field("activity") String activity);
    @FormUrlEncoded
    @POST("auth/getMenuData")
    Call<GetAds> adsOnlyActivity(@Field("activity") String activity);
    @FormUrlEncoded
    @POST("auth/getMenuData")
    Call<StatusActivityModel> getDataStatusActivity(@Field("activity") String activity,
                                                    @Field("index") Integer index
    );
    @FormUrlEncoded
    @POST("auth/getMenuData")
    Call<NewsActivityModel> getDataNewsActivity(@Field("activity") String activity,
                                                @Field("index") Integer index
    );
    @FormUrlEncoded
    @POST("auth/getMenuData")
    Call<CCTVActivityModel> getDataCCTVActivity(@Field("activity") String activity);
    @FormUrlEncoded
    @POST("auth/findNearby")
    Call<GetNearby> findNearby(@Field("latitude") Double latitude,
                               @Field("longitude") Double longitude,
                               @Field("radius") Integer radius,
                               @Field("id_category") String id_category
    );
    @FormUrlEncoded
    @POST("auth/login")
    Call<Login> login(@Field("username") String username,
                      @Field("password") String password
    );
    @FormUrlEncoded
    @POST("auth/register")
    Call<GetDataApiDefault> register(@Field("username") String username,
                                     @Field("password") String password,
                                     @Field("email") String email,
                                     @Field("name") String name
    );
    @FormUrlEncoded
    @POST("auth/forgotPassword")
    Call<GetDataApiDefault> forgotPassword(@Field("email") String email);
    @FormUrlEncoded
    @POST("auth/getStatusComment")
    Call<ListStatusComment> getStatusComment(@Field("id_status") String id_status);
    @FormUrlEncoded
    @POST("v1/getListAddUser")
    Call<ListUsers> getListAddUser(@Header ("Authorization") String token,
                                   @Field("id") String id
    );
    @FormUrlEncoded
    @POST("v1/getListUserFriend")
    Call<ListUsers> getListUserFriend(@Header ("Authorization") String token,
                                   @Field("id") String id
    );
    @FormUrlEncoded
    @POST("v1/addUser")
    Call<GetDataApiDefault> addUser(@Header ("Authorization") String token,
                                    @Field("id") String id,
                                    @Field("id_friend") String id_friend
    );
    @Multipart
    @POST("v1/createGroup")
    Call<GetDataApiDefault> createGroup(@Header ("Authorization") String token,
                                        @Part MultipartBody.Part image_file,
                                        @Part MultipartBody.Part id,
                                        @Part MultipartBody.Part group_member,
                                        @Part MultipartBody.Part group_name,
                                        @Part MultipartBody.Part id_chat_room
    );
    @Multipart
    @POST("v1/editGroup")
    Call<GetDataApiDefault> editGroup(@Header ("Authorization") String token,
                                        @Part MultipartBody.Part image_file,
                                        @Part MultipartBody.Part group_name,
                                        @Part MultipartBody.Part id,
                                        @Part MultipartBody.Part id_group
    );
    @FormUrlEncoded
    @POST("v1/getListChatGroup")
    Call<ListChatGroup> getListChatGroup(@Header ("Authorization") String token,
                                         @Field("id") String id
    );
    @FormUrlEncoded
    @POST("v1/getListContact")
    Call<ListContact> getListContact(@Header ("Authorization") String token,
                                     @Field("id") String id
    );
    @FormUrlEncoded
    @POST("v1/responseAdd")
    Call<GetDataApiDefault> responseAdd(@Header ("Authorization") String token,
                                        @Field("id") String id,
                                        @Field("id_friend") String id_friend,
                                        @Field("type") Integer type,
                                        @Field("response") Integer response
    );
    @Multipart
    @POST("v1/addUserStatus")
    Call<GetDataApiDefault> postStatus(@Header ("Authorization") String token,
                                       @Part MultipartBody.Part image_file,
                                       @Part MultipartBody.Part id_users,
                                       @Part MultipartBody.Part id_status_privacy,
                                       @Part MultipartBody.Part latitude,
                                       @Part MultipartBody.Part longitude,
                                       @Part MultipartBody.Part address,
                                       @Part MultipartBody.Part content,
                                       @Part MultipartBody.Part id_status,
                                       @Part MultipartBody.Part image_link_temp
    );
    @FormUrlEncoded
    @POST("v1/deleteUserStatus")
    Call<GetDataApiDefault> deleteStatus(@Header ("Authorization") String token,
                                         @Field("id_status") String id_status
    );
    @FormUrlEncoded
    @POST("v1/getUserNotification")
    Call<ListNotification> getNotification(@Header ("Authorization") String token,
                                           @Field("id") String id
    );
    @FormUrlEncoded
    @POST("v1/getUserNotificationWithIndex")
    Call<ListNotification> getNotificationWithIndex(@Header ("Authorization") String token,
                                                    @Field("id") String id,
                                                    @Field("index") Integer index
    );
    @FormUrlEncoded
    @POST("v1/getGroupNotification")
    Call<ListNotification> getGroupNotification(@Header ("Authorization") String token,
                                                @Field("id_group") String id_group
    );
    @FormUrlEncoded
    @POST("v1/getGroupMember")
    Call<ListUsers> getGroupMember(@Header ("Authorization") String token,
                                   @Field("id_group") String id_group
    );
    @FormUrlEncoded
    @POST("v1/getListInvite")
    Call<ListUsers> getListInvite(@Header ("Authorization") String token,
                                  @Field("id_group") String id_group,
                                  @Field("id_users") String id_users
    );

    @FormUrlEncoded
    @POST("v1/inviteToGroup")
    Call<GetDataApiDefault> inviteToGroup(@Header ("Authorization") String token,
                                          @Field("id_group") String id_group,
                                          @Field("group_member") String group_member
    );
    @FormUrlEncoded
    @POST("v1/addUserLocation")
    Call<GetDataApiDefault> addUserLocation(@Header ("Authorization") String token,
                         @Field("id_users") String id,
                         @Field("latitude") Double latitude,
                         @Field("longitude") Double longitude
    );
    @FormUrlEncoded
    @POST("v1/getLocationByGroup")
    Call<GetLocationByGroup> getGroupMemberLocation(@Header ("Authorization") String token,
                                                    @Field("id_group") String id,
                                                    @Field("latitude") Double latitude,
                                                    @Field("longitude") Double longitude
    );
    @FormUrlEncoded
    @POST("v1/userLeaveGroup")
    Call<GetDataApiDefault> leaveGroup(@Header ("Authorization") String token,
                                        @Field("id_group") String id_group,
                                        @Field("id_users") String id_users
    );
    @FormUrlEncoded
    @POST("v1/userUpdateProfileDetail")
    Call<GetDataApiDefault> updateProfileDetail(@Header ("Authorization") String token,
                                                @Field("id_users") String id_users,
                                                @Field("users_name") String users_name,
                                                @Field("users_username") String users_username
    );
    @Multipart
    @POST("v1/userUpdateProfilePicture")
    Call<GetDataApiDefault> updateProfilePicture(@Header ("Authorization") String token,
                                                 @Part MultipartBody.Part image_file,
                                                 @Part MultipartBody.Part id_users
    );
    @FormUrlEncoded
    @POST("v1/addUserCommentForStatus")
    Call<GetDataApiDefault> addUserComment(@Header ("Authorization") String token,
                                           @Field("id_users") String id_users,
                                           @Field("id_status") String id_status,
                                           @Field("content") String content
    );
    @FormUrlEncoded
    @POST("v1/getUserStatusFromNotification")
    Call<ListStatus.Data> getUserStatusFromNotification(@Header ("Authorization") String token,
                                           @Field("id_status") String id_status
    );
    @FormUrlEncoded
    @POST("v1/getListUserFriendById")
    Call<ListUsers> getListUserFriendById(@Header ("Authorization") String token,
                                                        @Field("id_users") String id_users
    );
    @FormUrlEncoded
    @POST("v1/getListUserGroupById")
    Call<ListUsers> getListUserGroupById(@Header ("Authorization") String token,
                                                @Field("id_users") String id_users
    );
    @FormUrlEncoded
    @POST("v1/getListUserStatusById")
    Call<ListStatus> getListUserStatusById(@Header ("Authorization") String token,
                                                @Field("id_users") String id_users
    );

}