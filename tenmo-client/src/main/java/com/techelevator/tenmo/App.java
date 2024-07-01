package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

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

        double currentBalance = restTemplate.getForObject(API_BASE_URL + "/current_balance/" + currentUser.getUser().getId(), double.class);
        System.out.println("Your current account balance is: $" + currentBalance);
		
	}

	private void viewTransferHistory() {
        List<Transfer> transferHistory = new ArrayList<>();
        Transfer[] transfers = restTemplate.getForObject(API_BASE_URL + "/transfer_history/" + currentUser.getUser().getId(), Transfer[].class);
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
		
	}

    private String getUserById(int account_id){
        String username = null;

        username = restTemplate.getForObject(API_BASE_URL + "/get_username_by_account_id", String.class);

        return username;
    }



	private void viewPendingRequests() {
        System.out.println(
                "-------------------------------------------\n" +
                "Pending Transfers\n" +
                "ID          To                     Amount\n" +
                "-------------------------------------------");

        Transfer[] transfers= restTemplate.getForObject(API_BASE_URL + "/pending_request", Transfer[].class);

        for (int i = 0; i < transfers.length; i++) {
            Transfer currentTransfer = transfers[i];
            System.out.println(currentTransfer.getAccount_to() + "          " + getUserById(currentTransfer.getAccount_to()) + "                 $" + currentTransfer.getAmount());
        }
		
	}

	private void sendBucks() {
		// TODO Auto-generated method stub
		
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

}
