package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import org.springframework.http.*;
import org.springframework.web.client.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class App {
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String API_BASE_URL = "http://localhost:8080";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }




    private void run() {
        consoleService.printGreeting();

        loginMenu();

        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                menuSelection = -1;
                consoleService.printGreeting();
                loginMenu();
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
        try{

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(currentUser.getToken());
            HttpEntity<String> entity = new HttpEntity<>(headers);

            BigDecimal currentBalance = restTemplate.exchange(API_BASE_URL + "/account/balance", HttpMethod.GET, entity, BigDecimal.class).getBody();
            System.out.println("Your current account balance is: $" + currentBalance);
        }catch (RestClientResponseException error){
            System.out.println(error.getResponseBodyAsString());
        }catch (ResourceAccessException error){
            System.out.println("Cannot connect");
        }


		
	}

	private void viewTransferHistory() {
        List<Transfer> transfersList = new ArrayList<>();

        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(currentUser.getToken());
            HttpEntity<String> entity = new HttpEntity<>(headers);

            Transfer [] transfers = restTemplate.exchange(API_BASE_URL + "/transfer/user", HttpMethod.GET, entity, Transfer[].class ).getBody();
            transfersList = new ArrayList<>(transfers.length);
            System.out.println(transfers.length);
            Collections.addAll(transfersList, transfers);
            System.out.println(transfersList.size());
        }catch(RestClientResponseException error){
            System.out.println(error.getResponseBodyAsString());
        }catch (ResourceAccessException error){
            System.out.println("Cannot access server");
        }

        //display all the transfer related to the user
        System.out.println("-------------------------------------------\n" +
                                      "Transfers\n" +
                           "ID          From/To                 Amount\n" +
                           "-------------------------------------------");

        for (int i = 0; i < transfersList.size(); i++) {
            Transfer currentTransfer = transfersList.get(i);

            try{

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(currentUser.getToken());

                HttpEntity<String> entity = new HttpEntity<>(headers);

                //get accountId from userId
                int ourAccount = restTemplate.exchange(API_BASE_URL + "/account/user", HttpMethod.GET, entity, int.class).getBody();

                if(currentTransfer.getAccount_from() == ourAccount){
                    System.out.println(currentTransfer.getTransfer_id()   + "          " + "To: "   + getUsernameById(currentTransfer.getAccount_to())   + "                 $" + currentTransfer.getAmount());

                }else{
                    System.out.println(currentTransfer.getTransfer_id() + "          " + "From: " + getUsernameById(currentTransfer.getAccount_from()) + "                 $" + currentTransfer.getAmount());

                }

            }catch (RestClientResponseException error){
                System.out.println(error.getResponseBodyAsString());
                System.out.println("error");
            }catch (ResourceAccessException error){
                System.out.println("Not connected");
            }
        }


        int userNumber = -1;
        while(userNumber != 0){
            userNumber = consoleService.promptForInt("Please enter transfer ID to view details (0 to cancel): ");

            if(userNumber == 0){
                break;
            }

            if(userNumber > 0){
                Transfer currentTransfer = null;
                String username;


                for(Transfer element: transfersList){
                    if(element.getTransfer_id() == userNumber){
                        currentTransfer = element;
                        break;
                    }
                }

                //stop at

                if(currentTransfer != null){
                    System.out.println(
                            "--------------------------------------------\n" +
                                    "Transfer Details\n" +
                            "--------------------------------------------");

                    //show Id
                    System.out.println("ID: " + currentTransfer.getTransfer_id());

                    HttpHeaders header = new HttpHeaders();
                    header.setContentType(MediaType.APPLICATION_JSON);
                    header.setBearerAuth(currentUser.getToken());

                    //for account_from user

                    username = getUsernameById(currentTransfer.getAccount_from());
                    System.out.println("From " + username);


                    //for account_to user
                    username = getUsernameById(currentTransfer.getAccount_to());
                    System.out.println("To " + username);


                    //for type
                    {
                        String type;
                        if(currentTransfer.getTransfer_type_id() == 1){
                            type = "Request";
                        }else{
                            type = "Send";
                        }
                        System.out.println("Type: " + type);
                    }

                    //for status
                    //transfer history only has approved or rejected status, not pending
                    {
                        String status;
                        if(currentTransfer.getTransfer_type_id() == 2){
                            status = "Approved";
                        }else{
                            status = "Rejected";
                        }
                        System.out.println("Type: " + status);
                    }

                    System.out.println("Amount: $" + currentTransfer.getAmount());



                }else{
                    System.out.println("Transfer Id does not exist");
                }
            }
            else {
                System.out.println("Cannot be negative");

            }
        }
		
	}

    private String getUsernameById(int account_id){
        String username = null;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(currentUser.getToken());

            HttpEntity<Integer> entity = new HttpEntity<>(account_id, headers);

            username = restTemplate.exchange(API_BASE_URL + "/user/id" , HttpMethod.POST, entity, String.class).getBody();

        }catch (RestClientResponseException error){
            System.out.println(error.getResponseBodyAsString());
        }
        return username;
    }



	private void viewPendingRequests() {
        System.out.println(
                "-------------------------------------------\n" +
                "Pending Transfers\n" +
                "ID          To                     Amount\n" +
                "-------------------------------------------");
        Transfer [] transfers;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        try {

            HttpEntity<String> entity = new HttpEntity<>(headers);

            transfers = restTemplate.exchange(API_BASE_URL + "/transfer/pending", HttpMethod.GET, entity, Transfer[].class).getBody();
        }
        catch (RestClientResponseException error) {
            System.out.println(error.getResponseBodyAsString());
            transfers = new Transfer[0];
        }catch (ResourceAccessException error){
            transfers = new Transfer[0];
        }

        for (int i = 0; i < transfers.length; i++) {
            Transfer currentTransfer = transfers[i];
            System.out.println(currentTransfer.getTransfer_id() + "          " + getUsernameById(currentTransfer.getAccount_to()) + "                 $" + currentTransfer.getAmount());
        }

        int userInputTransferId = -1;
        Transfer currentTransfer = null;
        while(userInputTransferId!=0 ) {

            String userInput = "";
            boolean check = false;
            while(!check) {
                userInput = ("Please enter transfer ID to approve/reject (0 to cancel): ");
                userInputTransferId = consoleService.promptForInt(userInput);

                //stop
                if (userInputTransferId == 0) {
                    break;
                }

                //if exist in the transfer list
                for (int i = 0; i < transfers.length; i++) {
                    if (transfers[i].getTransfer_id() == userInputTransferId) {
                        int userAccountId = 0;
                      try {
                          HttpEntity<String> entity = new HttpEntity<>(headers);
                          ResponseEntity<Integer> userAccount = restTemplate.exchange(API_BASE_URL + "/account/user", HttpMethod.GET, entity, int.class);
                           userAccountId = userAccount.getBody();
                      } catch(RestClientResponseException error) {
                          System.out.println(error.getResponseBodyAsString());
                      }catch (ResourceAccessException error) {
                          System.out.println("Can not read server");
                      }

                        if(transfers[i].getAccount_to() == userAccountId ) {
                            System.out.println("Can not modify your own request");
                        } else {
                            currentTransfer = transfers[i];
                            check = true;
                        }


                    }
                }

                if(check == false){
                    System.out.println("Not a valid transfer id");
                }
            }

            //not canceled, show other options
            if(userInputTransferId != 0) {

                userInput = "1: Approve\n" + "2: Reject\n" + "0: Don't approve or reject";
                int userStatusChoice = consoleService.promptForInt(userInput);

                //option 1: Approved
                if (userStatusChoice == 1) {
                    try{
                       // currentTransfer.setTransfer_status_id(2);

                        HttpEntity<Transfer> entity = new HttpEntity<>(currentTransfer, headers);

                        restTemplate.exchange(API_BASE_URL + "/transfer/pending/approved", HttpMethod.PUT, entity, Transfer.class).getBody();
                        System.out.println("Transfer status is updated to approved");
                    }catch (RestClientResponseException error){
                        System.out.println(error.getResponseBodyAsString());
                    }
                }

                //option 2: rejected
                if (userStatusChoice == 2) {
                  //  currentTransfer.setTransfer_status_id(3);

                    HttpEntity<Transfer> entity = new HttpEntity<>(currentTransfer, headers);
                    try {
                        restTemplate.exchange(API_BASE_URL + "/transfer/pending/reject" , HttpMethod.PUT, entity, Transfer.class);
                        System.out.println("Transfer status is updated to rejected");
                    } catch (RestClientResponseException error) {
                        System.out.println(error.getResponseBodyAsString());
                    }

                }


            }

        }

		
	}

	private void sendBucks() {
        System.out.println(
                "-------------------------------------------\n" +
                "Users\n" +
                "ID          Name\n" +
                "-------------------------------------------");
        User [] users;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(currentUser.getToken());

            HttpEntity<String> entity = new HttpEntity<>(headers);

            users = restTemplate.exchange(API_BASE_URL + "/user", HttpMethod.GET, entity, User[].class).getBody();

            //prints all user except themselves
            for(User element: users){

                if(element.getId() != currentUser.getUser().getId()) {
                    System.out.println(element.getId() + "          " + element.getUsername());
                }
            }

        }catch (RestClientResponseException error){
            System.out.println(error.getResponseBodyAsString());
            users = new User[0];
        }

        boolean check = false;
        String userInput = "";
        int sendToUserId = 0;
        while(!check) {
            System.out.println("Enter ID of user you are sending to (0 to cancel): ");

            sendToUserId = consoleService.promptForInt(userInput);

            if(sendToUserId == 0){
                break;
            }

            for(User element: users){

                if(element.getId() != sendToUserId) {
                    check = true;
                }
            }
            if(!check){
                System.out.println("User ID not found");
            }
        }

        System.out.println("Enter Amount:");
        BigDecimal amount = new BigDecimal(0);
        BigDecimal checkZero = new BigDecimal(0);

        while(amount.equals(checkZero) || amount.compareTo(checkZero) == -1){
            amount = consoleService.promptForBigDecimal(userInput);
        }



        try{
            Transfer createSendTransfer = new Transfer();
            createSendTransfer.setAccount_from(currentUser.getUser().getId());
            createSendTransfer.setAccount_to(sendToUserId);
            createSendTransfer.setTransfer_type_id(2);
            createSendTransfer.setTransfer_status_id(2);
            createSendTransfer.setAmount(amount);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(currentUser.getToken());

            HttpEntity<Transfer> entity = new HttpEntity<>(createSendTransfer, headers);

            Transfer sendTransfer = restTemplate.exchange(API_BASE_URL + "/transfer/send", HttpMethod.POST, entity, Transfer.class).getBody();
            System.out.println("Amount sent successfully");
        }catch(RestClientResponseException error){
            System.out.println(error.getResponseBodyAsString());
        } catch(ResourceAccessException error){
            System.out.println("Unable to connect to server");
        }catch(Exception e){

        }
		
	}

	private void requestBucks() {
        System.out.println(
                        "-------------------------------------------\n" +
                        "Users\n" +
                        "ID          Name\n" +
                        "-------------------------------------------");
        User [] users;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(currentUser.getToken());

            HttpEntity<String> entity = new HttpEntity<>(headers);

            users = restTemplate.exchange(API_BASE_URL + "/user", HttpMethod.GET, entity, User[].class).getBody();

            //prints all user except themselves
            for(User element: users){

                if(element.getId() != currentUser.getUser().getId()) {
                    System.out.println(element.getId() + "          " + element.getUsername());
                }
            }

        }catch (RestClientResponseException error){
            System.out.println(error.getResponseBodyAsString());
            users = new User[0];
        }

        boolean check = false;
        String userInput = "";
        int requestFromUser = 0;
        while(!check) {
            System.out.println("Enter ID of user you are requesting from (0 to cancel): ");

            requestFromUser = consoleService.promptForInt(userInput);

            if(requestFromUser == 0){
                break;
            }

            for(User element: users){

                if(element.getId() == requestFromUser) {
                    check = true;
                }
            }
            if (!check) {
                System.out.println("User not found");
            }

        }

        userInput = "";
        BigDecimal amount = new BigDecimal(0);
        amount = consoleService.promptForBigDecimal(userInput);

        Transfer createRequestTransfer = new Transfer();
        createRequestTransfer.setAccount_from(requestFromUser);
        createRequestTransfer.setAccount_to(currentUser.getUser().getId());
        createRequestTransfer.setTransfer_type_id(1);
        createRequestTransfer.setTransfer_status_id(1);
        createRequestTransfer.setAmount(amount);

        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(currentUser.getToken());

            HttpEntity<Transfer> entity = new HttpEntity<>(createRequestTransfer, headers);


            Transfer requestTransfer = restTemplate.exchange(API_BASE_URL + "/transfer/request", HttpMethod.PUT, entity, Transfer.class).getBody();

            System.out.println("Request sent");
        }catch(RestClientResponseException error){
            System.out.println(error.getResponseBodyAsString());
        } catch (ResourceAccessException error) {
            System.out.println("Unable to reach server");
        }
    }

}
