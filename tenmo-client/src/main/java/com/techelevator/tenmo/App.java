package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String API_BASE_URL = "http://localhost:8080/";

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
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {

        double currentBalance = restTemplate.exchange(API_BASE_URL + "/current_balance/" + currentUser.getUser().getId(), HttpMethod.GET, null, double.class).getBody();
        System.out.println("Your current account balance is: $" + currentBalance);
		
	}

	private void viewTransferHistory() {
        List<Transfer> transferHistory = new ArrayList<>();
        Transfer[] transfers = restTemplate.exchange(API_BASE_URL + "/transfer_history/" + currentUser.getUser().getId(), HttpMethod.GET, null, Transfer[].class ).getBody();
        System.out.println("-------------------------------------------\n" +
                                      "Transfers\n" +
                           "ID          From/To                 Amount\n" +
                           "-------------------------------------------");
        for (int i = 0; i < transfers.length; i++) {
            Transfer currentTransfer = transfers[i];

            if(currentTransfer.getAccount_from() == currentUser.getUser().getId()){
                System.out.println(currentTransfer.getAccount_to() + "          " + "To: " + getUserById(currentTransfer.getAccount_to()) + "                 $" + currentTransfer.getAmount());

            }else{
                System.out.println(currentTransfer.getAccount_from() + "          " + "From: " + getUserById(currentTransfer.getAccount_from()) + "                 $" + currentTransfer.getAmount());

            }

        }

        int userNumber = -1;
        while(userNumber != 0){
            System.out.println("Please enter transfer ID to view details (0 to cancel): ");
            String userInput = consoleService.getScanner().nextLine();
            try{
                userNumber = Integer.parseInt(userInput);
            }catch(NumberFormatException error){
                System.out.println("Invalid number");
                continue;
            }

            if(userNumber == 0){
                break;
            }

            if(userNumber > 0){

                for (int i = 0; i < transfers.length; i++) {
                    if (transfers[i].getTransfer_id() == userNumber) {
                        System.out.println(
                                "--------------------------------------------\n" +
                                        "Transfer Details\n" +
                                        "--------------------------------------------");
                        //needs a transfer.toString()
                    }
                }
            }
            else {
                System.out.println("Cannot be negative");

            }

        }
		
	}

    private String getUserById(int account_id){
        String username = null;

        username = restTemplate.exchange(API_BASE_URL + "/get_username_by_account_id/" + account_id, HttpMethod.GET, null, String.class).getBody();

        return username;
    }



	private void viewPendingRequests() {
        System.out.println(
                "-------------------------------------------\n" +
                "Pending Transfers\n" +
                "ID          To                     Amount\n" +
                "-------------------------------------------");

        Transfer[] transfers= restTemplate.exchange(API_BASE_URL + "/pending_request", HttpMethod.GET, null,Transfer[].class).getBody();

        for (int i = 0; i < transfers.length; i++) {
            Transfer currentTransfer = transfers[i];
            System.out.println(currentTransfer.getAccount_to() + "          " + getUserById(currentTransfer.getAccount_to()) + "                 $" + currentTransfer.getAmount());
        }

        int userNumber = -1;
        int transfer_id = -1;

        while(userNumber!=0) {

            System.out.println("Please enter transfer ID to approve/reject (0 to cancel): ");

            String userInput = consoleService.getScanner().nextLine();


            try{
                transfer_id = Integer.parseInt(userInput);
            }catch(NumberFormatException error){
                System.out.println("Invalid number");
                continue;
            }

            if( transfer_id == 0){
                break;
            }


            System.out.println(
                    "1: Approve\n" + "2: Reject\n" + "0: Don't approve or reject");

            userInput = consoleService.getScanner().nextLine();

            try{
                userNumber = Integer.parseInt(userInput);
            }catch(NumberFormatException error){
                System.out.println("Invalid number");
                continue;
            }

            if(userNumber == 1){
                sendBucks(transfer_id);
            }
            if(userNumber == 2){
                transfers[userNumber].setTransfer_status_id(3);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Transfer> entity = new HttpEntity<>( transfers[userNumber], headers);

                restTemplate.exchange(API_BASE_URL + "/pending_update/" + transfers[userNumber].getTransfer_id(), HttpMethod.PUT, entity,void.class);

            }



        }

		
	}

	private void sendBucks() {
		// TODO Auto-generated method stub
		
	}

    private void sendBucks(int transfer_id) {


        // TODO Auto-generated method stub

    }



	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

}
