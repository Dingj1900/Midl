import axios from 'axios';

export default{


    registerUser(registerUser){
    
      return  axios.post("/register",registerUser);


    }




}




