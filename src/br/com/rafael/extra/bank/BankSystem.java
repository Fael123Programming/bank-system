package br.com.rafael.extra.bank;

import br.com.rafael.extra.accounts.owners.concrete.*;
import br.com.rafael.extra.accounts.types._abstract.Account;
import br.com.rafael.extra.accounts.types.concrete.*;
import br.com.rafael.extra.exceptions.*;
import br.com.rafael.extra.view.View;
import br.com.rafael.extra.persistence.FileHandler;
import br.com.rafael.resourcebundles.bundlepool.BundlePool;

import java.io.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class BankSystem {
    private static final String AGENCY = "MAIN-AGENCY";
    private final Map<Integer, Account> accounts;
    private final String filePath;
    private final Locale baseLocale;

    public BankSystem(String filePath, Locale baseLocale) {
        BundlePool.setBaseLocale(baseLocale);
        File accountFile = new File(filePath);
        if (!accountFile.exists()) {
            try {
                if (!accountFile.createNewFile()) {
                    View.showMessage(BundlePool.getInstance().getExceptionBundle().getString("impossibleToCreateFile"));
                    View.exit();
                }
            } catch (IOException exc) {
                View.showMessage(BundlePool.getInstance().getExceptionBundle().getString("impossibleToCreateFile"));
                View.exit();
            }
        }
        this.filePath = filePath;
        this.baseLocale = baseLocale;
        this.accounts = new HashMap<>();
        Account account;
        //Bringing all accounts back from the file to the attribute 'accounts'.
        try {
            Scanner fileScan = new Scanner(accountFile);
            while (fileScan.hasNextLine()) {
                account = Account.parseString(fileScan.nextLine());
                this.accounts.put(account.getAccountNumber(), account);
            }
        } catch(FileNotFoundException exc) {
            //Yes... nothing should be done here. Actually, this is never going to be reached.
        } catch (NonConvertibleStringAccount exc) {
            View.showMessage(BundlePool.getInstance().getExceptionBundle().getString("nonConvertibleStringAccount"));
            View.exit();
        }
    }

    public void start() {
        while (true) {
            try {
                switch (View.menu(BundlePool.getInstance().getMenuBundle().getString("mainMenu"))) {
                    case 1 -> createAccount();
                    case 2 -> informationOfAnAccount();
                    case 3 -> movementAccount();//Here withdraw and deposit methods were implemented
                    case 4 -> {
                        //Recording all accounts back into the file.
                        FileHandler.clean(this.filePath);
                        for (Account account : this.accounts.values())
                            FileHandler.appendTo(this.filePath, account.toString());
                        View.exit();
                    }
                    default -> View.showMessage(BundlePool.getInstance().getExceptionBundle().getString("invalidChoice"));
                }
            } catch (NumberFormatException exc) {
                View.showMessage(BundlePool.getInstance().getExceptionBundle().getString("invalidChoice"));
            } catch (FileNotFoundException exc) {
                //Yes... nothing should be done here. Actually, this is never going to be reached due line 23.
            } catch (IOException exc) {
                View.showMessage(BundlePool.getInstance().getExceptionBundle().getString("recordAccountsExc"));
            }
        }
    }

    private void createAccount() {
        Account newAccount;
        String ownerName, cpf, date;
        int typeOfAccount;
        double valueEspecialCheck;
        while (true) {
            try {
                typeOfAccount = View.menu(BundlePool.getInstance().getMenuBundle().getString("accountCreationMenu"));
                switch (typeOfAccount) {
                    case 1 -> {
                        ownerName = View.inputDialog(BundlePool.getInstance().getAccountCreationBundle().getString("createNewCurrentAccount") + "\n" + BundlePool.getInstance().getAccountCreationBundle().getString("insertName"));
                        cpf = View.inputDialog(BundlePool.getInstance().getAccountCreationBundle().getString("createNewCurrentAccount") + "\n" + BundlePool.getInstance().getAccountCreationBundle().getString("insertCPF"));
                        date = View.inputDialog(BundlePool.getInstance().getAccountCreationBundle().getString("createNewCurrentAccount") + "\n" + BundlePool.getInstance().getAccountCreationBundle().getString("insertBirthdate"));
                        newAccount = new CurrentAccount(new PhysicalPerson(ownerName, LocalDate.parse(date), cpf), View.getNewAccountNumber(this.accounts), BankSystem.AGENCY);
                        View.showMessage(BundlePool.getInstance().getAccountCreationBundle().getString("currentAccountCreated") + "\n" + BundlePool.getInstance().getAccountCreationBundle().getString("accountNumber") + ": " + newAccount.getAccountNumber());
                        this.accounts.put(newAccount.getAccountNumber(), newAccount);
                    }
                    case 2 -> {
                        ownerName = View.inputDialog(BundlePool.getInstance().getAccountCreationBundle().getString("createNewSavingsAccount") + "\n" + BundlePool.getInstance().getAccountCreationBundle().getString("insertName"));
                        cpf = View.inputDialog(BundlePool.getInstance().getAccountCreationBundle().getString("createNewSavingsAccount") + "\n" + BundlePool.getInstance().getAccountCreationBundle().getString("insertCPF"));
                        date = View.inputDialog(BundlePool.getInstance().getAccountCreationBundle().getString("createNewSavingsAccount") + "\n" + BundlePool.getInstance().getAccountCreationBundle().getString("insertBirthdate"));
                        newAccount = new SavingsAccount(new PhysicalPerson(ownerName, LocalDate.parse(date), cpf), View.getNewAccountNumber(this.accounts), BankSystem.AGENCY);
                        View.showMessage(BundlePool.getInstance().getAccountCreationBundle().getString("savingsAccountCreated") + "\n" + BundlePool.getInstance().getAccountCreationBundle().getString("accountNumber") + ": " + newAccount.getAccountNumber() +
                                "\n" + BundlePool.getInstance().getAccountCreationBundle().getString("yieldPercentage") + ": " + SavingsAccount.getYieldPercentage().movePointRight(2));
                        this.accounts.put(newAccount.getAccountNumber(), newAccount);
                    }
                    case 3 -> {
                        ownerName = View.inputDialog(BundlePool.getInstance().getAccountCreationBundle().getString("createNewSpecialAccountPP") + "\n" + BundlePool.getInstance().getAccountCreationBundle().getString("insertName"));
                        cpf = View.inputDialog(BundlePool.getInstance().getAccountCreationBundle().getString("createNewSpecialAccountPP") + "\n" + BundlePool.getInstance().getAccountCreationBundle().getString("insertCPF"));
                        date = View.inputDialog(BundlePool.getInstance().getAccountCreationBundle().getString("createNewSpecialAccountPP") + "\n" + BundlePool.getInstance().getAccountCreationBundle().getString("insertBirthdate"));
                        valueEspecialCheck = View.inputDialogForFloatNumber(BundlePool.getInstance().getAccountCreationBundle().getString("createNewSpecialAccountPP") + "\n" + BundlePool.getInstance().getAccountCreationBundle().getString("insertSpecialCheckValue"));
                        newAccount = new SpecialAccount(new PhysicalPerson(ownerName, LocalDate.parse(date), cpf),
                                View.getNewAccountNumber(this.accounts), BankSystem.AGENCY, new BigDecimal(valueEspecialCheck));
                        View.showMessage(BundlePool.getInstance().getAccountCreationBundle().getString("specialAccountPPCreated") + "\n" + BundlePool.getInstance().getAccountCreationBundle().getString("accountNumber") + ": " + newAccount.getAccountNumber()
                                + "\n" + BundlePool.getInstance().getAccountCreationBundle().getString("specialCheck") + ": " + NumberFormat.getCurrencyInstance(this.baseLocale).format(newAccount.getValueEspecialCheck().doubleValue()));
                        this.accounts.put(newAccount.getAccountNumber(), newAccount);
                    }
                    case 4 -> {
                        String cnpj;
                        ownerName = View.inputDialog(BundlePool.getInstance().getAccountCreationBundle().getString("createNewSpecialAccountLP") + "\n" + BundlePool.getInstance().getAccountCreationBundle().getString("insertName"));
                        cnpj = View.inputDialog(BundlePool.getInstance().getAccountCreationBundle().getString("createNewSpecialAccountLP") + "\n" + BundlePool.getInstance().getAccountCreationBundle().getString("insertCNPJ"));
                        date = View.inputDialog(BundlePool.getInstance().getAccountCreationBundle().getString("createNewSpecialAccountLP") + "\n" + BundlePool.getInstance().getAccountCreationBundle().getString("insertDateOfCreation"));
                        valueEspecialCheck = View.inputDialogForFloatNumber(BundlePool.getInstance().getAccountCreationBundle().getString("createNewSpecialAccountLP") + "\n" + BundlePool.getInstance().getAccountCreationBundle().getString("insertSpecialCheckValue"));
                        newAccount = new BusinessAccount(new LegalPerson(ownerName, LocalDate.parse(date), cnpj),View.getNewAccountNumber(this.accounts), BankSystem.AGENCY, new BigDecimal(valueEspecialCheck));
                        View.showMessage(BundlePool.getInstance().getAccountCreationBundle().getString("specialAccountLPCreated") + "\n" + BundlePool.getInstance().getAccountCreationBundle().getString("accountNumber") + ": " + newAccount.getAccountNumber() +
                                "\n" + BundlePool.getInstance().getAccountCreationBundle().getString("specialCheck") + ": " + NumberFormat.getCurrencyInstance(this.baseLocale).format(newAccount.getValueEspecialCheck().doubleValue()));
                        this.accounts.put(newAccount.getAccountNumber(), newAccount);
                    }
                    case 5 -> {
                        return;
                    }
                    default -> View.showMessage(BundlePool.getInstance().getExceptionBundle().getString("invalidChoice"));
                }
            } catch (NumberFormatException | NullPointerException ignore) {
                View.showMessage(BundlePool.getInstance().getExceptionBundle().getString("invalidData"));
            } catch (ArrayIndexOutOfBoundsException  | DateTimeParseException ignore) {
                View.showMessage(BundlePool.getInstance().getExceptionBundle().getString("invalidDate"));
            }
        }
    }

    private void informationOfAnAccount() {
        try {
            if (this.accounts.isEmpty()) {
                View.showMessage(BundlePool.getInstance().getExceptionBundle().getString("noAccounts"));
                return;
            }
            int numberOfAccount = View.inputDialogForIntegerNumber(BundlePool.getInstance().getAccountInfoBundle().getString("accountInformation") +
                    "\n" + BundlePool.getInstance().getAccountInfoBundle().getString("enterAccountNumber"));
            if (!this.accounts.containsKey(numberOfAccount)) {
                View.showMessage(BundlePool.getInstance().getExceptionBundle().getString("accountNotFound"));
                return;
            }
            Account requiredAccount = this.accounts.get(numberOfAccount);
            View.showMessage(BundlePool.getInstance().getAccountInfoBundle().getString("accountInformation") + "\n" + requiredAccount.getStandardized());
        } catch (NumberFormatException exception) {
            View.showMessage(BundlePool.getInstance().getExceptionBundle().getString("invalidData"));
        }
    }

    private void movementAccount() throws FileNotFoundException {
        if (this.accounts.isEmpty()) {
            View.showMessage(BundlePool.getInstance().getExceptionBundle().getString("noAccounts"));
            return;
        }
        while (true) {
            try {
                switch (View.menu(BundlePool.getInstance().getMenuBundle().getString("accountMoveMenu"))) {
                    case 1 -> withDrawMoneyOfAnAccount();
                    case 2 -> depositMoneyInAnAccount();
                    case 3 -> transferMoneyBetweenAccounts();
                    case 4 -> { return; }
                    default -> View.showMessage(BundlePool.getInstance().getExceptionBundle().getString("invalidChoice"));
                }
            } catch (NumberFormatException exc) {
                View.showMessage(BundlePool.getInstance().getExceptionBundle().getString("invalidData"));
            } catch (AccountNotFoundException | InsufficientFundsException | IllegalArgumentException exception) {
                View.showMessage(exception.getMessage());
            }
        }
    }

    private void withDrawMoneyOfAnAccount() throws AccountNotFoundException, IllegalArgumentException, InsufficientFundsException {
        int numberOfAccount = View.inputDialogForIntegerNumber("<<<<< Sacar quantia de uma conta >>>>>\n"
                + "Insira o numero da conta");
        if (!this.accounts.containsKey(numberOfAccount))
            throw new AccountNotFoundException(BundlePool.getInstance().getExceptionBundle().getString("accountNotFound"));
        Account requiredAccount = this.accounts.get(numberOfAccount);
        String amountToGet = View.inputDialog(String.format("<<<<< Sacar quantia de uma conta >>>>>%n"
                + "%s%nInsira a quantia a sacar", requiredAccount.getStandardized()));
        requiredAccount.withdraw(new BigDecimal(amountToGet)); //This line can throw an InsufficientFundsException or an IllegalArgumentException!
        View.showMessage(String.format("<<<<< Sacar quantia de uma conta >>>>>%nSaque efetuado com sucesso!%n" +
                "Saldo atual da conta: R$ %s", requiredAccount.getCurrentBalance().toString())); //If everything went as expected, we'll get here!
    }

    private void depositMoneyInAnAccount() throws AccountNotFoundException, IllegalArgumentException {
        int numberOfAccount = View.inputDialogForIntegerNumber("<<<<< Depositar quantia em uma conta >>>>>\nInsira o numero da conta");
        if (!this.accounts.containsKey(numberOfAccount))
            throw new AccountNotFoundException(BundlePool.getInstance().getExceptionBundle().getString("accountNotFound"));
        Account requiredAccount = this.accounts.get(numberOfAccount);
        String amountToDeposit = View.inputDialog(String.format("<<<<< Depositar quantia em uma conta >>>>>%n%s%nInsira a quantia a depositar", requiredAccount.getStandardized()));
        requiredAccount.deposit(new BigDecimal(amountToDeposit)); //This line can throw an IllegalArgumentException
        View.showMessage(String.format("<<<<< Depositar quantia em uma conta >>>>>%nDeposito efetuado com sucesso!%nSaldo atual da conta: R$ %s", requiredAccount.getCurrentBalance().toString()));
    }

    private void transferMoneyBetweenAccounts() throws AccountNotFoundException, InsufficientFundsException, IllegalArgumentException {
        if (this.accounts.size() <= 1) {
            View.showMessage(BundlePool.getInstance().getExceptionBundle().getString("cannotTransfer"));
            return;
        }
        int numberOfAccountToTransferWith = View.inputDialogForIntegerNumber(String.format("<<<<< Transferir"
                + " de uma conta para outra >>>>>%nInsira o numero da conta depositante"));
        if (!this.accounts.containsKey(numberOfAccountToTransferWith))
            throw new AccountNotFoundException(BundlePool.getInstance().getExceptionBundle().getString("accountNotFound"));
        int numberOfAccountToReceiveAmount = View.inputDialogForIntegerNumber(String.format("<<<<< Transferir"
                + " de uma conta para outra >>>>>%nInsira o numero da conta receptora"));
        if (!this.accounts.containsKey(numberOfAccountToReceiveAmount))
            throw new AccountNotFoundException(BundlePool.getInstance().getExceptionBundle().getString("accountNotFound"));
        Account toTransferWith = this.accounts.get(numberOfAccountToTransferWith);
        Account toReceiveAmount = this.accounts.get(numberOfAccountToReceiveAmount);
        String amountToTransfer = View.inputDialog(String.format("<<<<< Transferir de uma conta para outra >>>>>%n%n> " +
                "Conta depositante <%n%s%n> Conta receptora <%n%s%nInsira a quantia a transferir", toTransferWith.getStandardized(),toReceiveAmount.getStandardized()));
        toTransferWith.transfer(toReceiveAmount, new BigDecimal(amountToTransfer));
        View.showMessage(String.format("<<<<< Transferencia realizada com sucesso! >>>>>%nConta depositante: saldo atual " +
                "(R$ %s)%nConta receptora: saldo atual (R$ %s)", toTransferWith.getCurrentBalance().toString(), toReceiveAmount.getCurrentBalance().toString()));
    }
}
