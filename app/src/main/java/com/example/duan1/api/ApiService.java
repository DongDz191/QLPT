package com.example.duan1.api;

import com.example.duan1.model.Account;
import com.example.duan1.model.Contract;
import com.example.duan1.model.Invoice;
import com.example.duan1.model.Member;
import com.example.duan1.model.Room;
import com.example.duan1.model.RoomType;
import com.example.duan1.model.Service;
import com.example.duan1.model.ServiceDetail;
import com.example.duan1.model.Utility;
import com.example.duan1.model.UtilityDetail;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // Room endpoints
    @GET("api/rooms")
    Call<List<Room>> getAllRooms();

    @POST("api/rooms")
    Call<Void> insertRoom(@Body Room room);

    @PUT("api/rooms/{roomCode}")
    Call<Void> updateRoom(@Path("roomCode") String roomCode, @Body Room room);

    @DELETE("api/rooms/{roomCode}")
    Call<Void> deleteRoom(@Path("roomCode") String roomCode);

    @GET("api/rooms/{roomCode}")
    Call<Room> getRoomByRoomCode(@Path("roomCode") String roomCode);

    @GET("api/rooms/check/{roomCode}")
    Call<Room> checkRoom(@Path("roomCode") String roomCode);

    @GET("api/rooms/search")
    Call<List<Room>> searchRoom(@Query("search") String search);

    @GET("api/rooms/filter")
    Call<List<Room>> filterRoom(@Query("search") String search);

    // Account endpoints
    @POST("api/accounts")
    Call<Long> insertAccount(@Body Account account);

    @PUT("api/accounts/{username}")
    Call<Void> updateAccount(@Path("username") String username, @Body Account account);

    @DELETE("api/accounts/{username}")
    Call<Void> deleteAccount(@Path("username") String username);

    @GET("api/accounts")
    Call<List<Account>> getAllAccounts();

    @POST("api/accounts/login")
    Call<Account> checkLogin(@Query("username") String username, @Query("password") String password);

    @GET("api/accounts/check-username")
    Call<Account> checkUsername(@Query("username") String username);

    @PUT("api/accounts/update-password")
    Call<Boolean> updatePassword(@Query("username") String username, @Query("newPassword") String newPassword);

    // RoomType endpoints
    @POST("api/roomtypes")
    Call<Void> insertRoomType(@Body RoomType roomType);

    @PUT("api/roomtypes/{idRoomType}")
    Call<Void> updateRoomType(@Path("idRoomType") int idRoomType, @Body RoomType roomType);

    @DELETE("api/roomtypes/{idRoomType}")
    Call<Void> deleteRoomType(@Path("idRoomType") int idRoomType);

    @GET("api/roomtypes")
    Call<List<RoomType>> getAllRoomTypes();

    @GET("api/roomtypes/{idRoomType}")
    Call<RoomType> getRoomTypeById(@Path("idRoomType") int idRoomType);

    @GET("api/roomtypes/newest")
    Call<RoomType> getRoomTypeNewest();

    @GET("api/roomtypes/by-contract/{idContract}")
    Call<RoomType> getRTByIdContract(@Path("idContract") int idContract);

    // UtilityDetail endpoints
    @POST("api/utilitydetails")
    Call<Void> insertUtilityDetail(@Body UtilityDetail utilityDetail);

    @PUT("api/utilitydetails")
    Call<Void> updateUtilityDetail(@Body UtilityDetail utilityDetail);

    @DELETE("api/utilitydetails")
    Call<Void> deleteUtilityDetail(@Body UtilityDetail utilityDetail);

    @GET("api/utilitydetails")
    Call<List<UtilityDetail>> getAllUtilityDetails();

    @GET("api/utilitydetails/utilities/{idRoomType}")
    Call<List<Utility>> getUtilitiesByRoomType(@Path("idRoomType") int idRoomType);

    @GET("api/utilitydetails/utility-names/{idRoomType}")
    Call<List<String>> getUtilityNamesByRoomType(@Path("idRoomType") int idRoomType);

    // Utility endpoints
    @POST("api/utilities")
    Call<Void> insertUtility(@Body Utility utility);

    @PUT("api/utilities/{idUtility}")
    Call<Void> updateUtility(@Path("idUtility") int idUtility, @Body Utility utility);

    @DELETE("api/utilities/{idUtility}")
    Call<Void> deleteUtility(@Path("idUtility") int idUtility);

    @GET("api/utilities")
    Call<List<Utility>> getAllUtilities();

    // Service endpoints
    @POST("api/services")
    Call<Void> insertService(@Body Service service);

    @PUT("api/services/{idService}")
    Call<Void> updateService(@Path("idService") int idService, @Body Service service);

    @DELETE("api/services/{idService}")
    Call<Void> deleteService(@Path("idService") int idService);

    @GET("api/services")
    Call<List<Service>> getAllServices();

    @GET("api/services/search")
    Call<List<Service>> searchServices(@Query("keyword") String keyword);

    @GET("api/services/{idService}")
    Call<Service> getServiceById(@Path("idService") int idService);

    // Contract endpoints
    @POST("api/contracts")
    Call<Void> insertContract(@Body Contract contract);

    @PUT("api/contracts/{idContract}")
    Call<Void> updateContract(@Path("idContract") int idContract, @Body Contract contract);

    @DELETE("api/contracts/{idContract}")
    Call<Void> deleteContract(@Path("idContract") int idContract);

    @GET("api/contracts")
    Call<List<Contract>> getAllContracts();

    @GET("api/contracts/{idContract}")
    Call<Contract> getContractById(@Path("idContract") int idContract);

    @GET("api/contracts/newest")
    Call<Contract> getContractNewest();

    @GET("api/contracts/room/{roomCode}")
    Call<Contract> getContractByRoomCode(@Path("roomCode") String roomCode);

    @GET("api/contracts/search/wait")
    Call<List<Contract>> getSearchWait(@Query("date") String date, @Query("keyword") String keyword);

    @GET("api/contracts/search/filter-wait")
    Call<List<Contract>> getSearchFilterWait(@Query("date") String date, @Query("date2") String date2, @Query("keyword") String keyword);

    @GET("api/contracts/search/effect")
    Call<List<Contract>> getSearchEffect(@Query("date") String date, @Query("keyword") String keyword);

    @GET("api/contracts/search/filter-effect")
    Call<List<Contract>> getSearchFilterEffect(@Query("date") String date, @Query("date2") String date2, @Query("keyword") String keyword);

    @GET("api/contracts/search/expire")
    Call<List<Contract>> getSearchExpire(@Query("keyword") String keyword);

    @GET("api/contracts/search/filter-expire")
    Call<List<Contract>> getSearchFilterExpire(@Query("date2") String date2, @Query("keyword") String keyword);

    // Member endpoints
    @POST("api/members")
    Call<Void> insertMember(@Body Member member);

    @PUT("api/members/{idMember}")
    Call<Void> updateMember(@Path("idMember") int idMember, @Body Member member);

    @DELETE("api/members/{idMember}")
    Call<Void> deleteMember(@Path("idMember") int idMember);

    @GET("api/members")
    Call<List<Member>> getAllMembers();

    @GET("api/members/contract/{idContract}")
    Call<List<Member>> getMemberByIdContract(@Path("idContract") int idContract);


    // Invoice endpoints
    @POST("api/invoices")
    Call<Void> insertInvoice(@Body Invoice invoice);

    @PUT("api/invoices/{idInvoice}")
    Call<Void> updateInvoice(@Path("idInvoice") int idInvoice, @Body Invoice invoice);

    @DELETE("api/invoices/{idInvoice}")
    Call<Void> deleteInvoice(@Path("idInvoice") int idInvoice);

    @GET("api/invoices")
    Call<List<Invoice>> getAllInvoices();

    @GET("api/invoices/between-dates")
    Call<List<Invoice>> getInvoicesBetweenDates(@Query("startDate") String startDate, @Query("endDate") String endDate);

    @GET("api/invoices/now/{idContract}")
    Call<Invoice> getInvoiceNow(@Path("idContract") int idContract);

    @GET("api/invoices/contract/{idContract}")
    Call<List<Invoice>> getInvoiceByIdContract(@Path("idContract") int idContract);

    @GET("api/invoices/not-pay/{idContract}")
    Call<Invoice> getInvoiceNotPayByIdContract(@Path("idContract") int idContract);






    // ServiceDetail endpoints
    @POST("api/servicedetails")
    Call<Void> insertServiceDetail(@Body ServiceDetail serviceDetail);

    @PUT("api/servicedetails/{idServiceDetail}")
    Call<Void> updateServiceDetail(@Path("idServiceDetail") int idServiceDetail, @Body ServiceDetail serviceDetail);

    @DELETE("api/servicedetails/{idServiceDetail}")
    Call<Void> deleteServiceDetail(@Path("idServiceDetail") int idServiceDetail);

    @GET("api/servicedetails")
    Call<List<ServiceDetail>> getAllServiceDetails();

    @GET("api/servicedetails/invoice/{idInvoice}")
    Call<List<ServiceDetail>> getServiceDetailByIdInvoice(@Path("idInvoice") int idInvoice);
}

