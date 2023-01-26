package sg.nus.iss.team7.buspooling.mobileadapp.retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sg.nus.iss.team7.buspooling.mobileadapp.model.CustomerDTO;

public interface ApiMethods {


    @POST("/api/customer/login")
    Call<CustomerDTO> loginCustomer(@Body CustomerDTO customerDTO);

    @POST("/api/customer/register")
    Call<CustomerDTO> registerNewCustomer(@Body CustomerDTO customerDTO);

}
