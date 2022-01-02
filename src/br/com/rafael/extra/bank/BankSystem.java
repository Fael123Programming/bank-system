package br.com.rafael.extra.bank;

import br.com.rafael.extra.accounts.owners.concrete.*;
import br.com.rafael.extra.accounts.types._abstract.Account;
import br.com.rafael.extra.accounts.types.concrete.*;
import br.com.rafael.extra.exceptions.*;
import br.com.rafael.extra.view.View;
import br.com.rafael.extra.persistence.FileHandler;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class BankSystem {
    private static final String AGENCY = "MAIN-AGENCY";
    private final Map<Integer, Account> accounts;
    private final String filePath;
    private final ResourceBundle menus, exceptions;

    public BankSystem(String filePath, Locale baseLocale) {
        this.exceptions = ResourceBundle.getBundle("br.com.rafael.resource_bundles.exceptions", baseLocale);
        File accountFile = new File(filePath);
        if (!accountFile.exists()) {
            try {
                if (!accountFile.createNewFile()) {
                    View.showMessage(this.exceptions.getString("impossibleToCreateFile"));
                    View.exit();
                }
            } catch (IOException exc) {
                View.showMessage(this.exceptions.getString("impossibleToCreateFile"));
                View.exit();
            }
        }
        this.menus = ResourceBundle.getBundle("br.com.rafael.resource_bundles.menus", baseLocale);
        this.filePath = filePath;
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
            View.showMessage(this.exceptions.getString("nonConvertibleStringAccount"));
            View.exit();
        }
    }

    public void start() {
        while (true) {
            try {
                switch (View.menu(this.menus.getString("mainMenu"))) {
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
                    default -> View.showMessage(this.exceptions.getString("invalidChoice"));
                }
            } catch (NumberFormatException exc) {
                View.showMessage(this.exceptions.getString("invalidChoice"));
            } catch (FileNotFoundException exc) {
                //Yes... nothing should be done here. Actually, this is never going to be reached due line 23.
            } catch (IOException exc) {
                View.showMessage(this.exceptions.getString("recordAccountsExc"));
            }
        }
    }

    private void createAccount() {
        Account newAccount;
        String ownerName, cpf, dateOfBirth;
        int typeOfAccount;
        double valueEspecialCheck;
        while (true) {
            try {
                typeOfAccount = View.menu(this.menus.getString("accountCreationMenu"));
                switch (typeOfAccount) {
                    case 1 -> {
                        ownerName = View.inputDialog("<<<<< Criar Nova Conta Corrente >>>>>\nInsira seu nome");
                        cpf = View.inputDialog("<<<<< Criar Nova Conta Corrente >>>>>\nInsira seu CPF");
                        dateOfBirth = View.inputDialog("<<<<< Criar Nova Conta Corrente >>>>>\nInsira sua data de nascimento (formato aaaa-mm-dd)");
                        newAccount = new CurrentAccount(new PhysicalPerson(ownerName, LocalDate.of(Integer.parseInt(dateOfBirth.split("-")[0].trim()),
                                Integer.parseInt(dateOfBirth.split("-")[1].trim()), Integer.parseInt(dateOfBirth.split("-")[2].trim())), cpf), View.getNewAccountNumber(this.accounts), BankSystem.AGENCY);
                        View.showMessage(String.format("<<<<< Conta Corrente criada com sucesso >>>>>\n Numero da conta: %d", newAccount.getAccountNumber()));
                        this.accounts.put(newAccount.getAccountNumber(), newAccount);
                    }
                    case 2 -> {
                        ownerName = View.inputDialog("<<<<< Criar Nova Conta Poupança >>>>>\nInsira seu nome");
                        cpf = View.inputDialog("<<<<< Criar Nova Conta Poupança >>>>>\nInsira seu CPF");
                        dateOfBirth = View.inputDialog("<<<<< Criar Nova Conta Poupança >>>>>\nInsira sua data de nascimento (formato aaaa-mm-dd)");
                        newAccount = new SavingsAccount(new PhysicalPerson(ownerName, LocalDate.of(Integer.parseInt(dateOfBirth.split("-")[0].trim()),
                                Integer.parseInt(dateOfBirth.split("-")[1].trim()), Integer.parseInt(dateOfBirth.split("-")[2].trim())), cpf), View.getNewAccountNumber(this.accounts), BankSystem.AGENCY);
                        View.showMessage(String.format("<<<<< Conta Poupanca criada com sucesso >>>>>\nNumero da conta: %d\nRendimento: %s%%", newAccount.getAccountNumber(), SavingsAccount.getYieldPercentage().movePointRight(2)));
                        this.accounts.put(newAccount.getAccountNumber(), newAccount);
                    }
                    case 3 -> {
                        ownerName = View.inputDialog("<<<<< Criar Nova Conta Especial (Pessoa Fisica) >>>>>\nInsira seu nome");
                        cpf = View.inputDialog("<<<<< Criar Nova Conta Especial (Pessoa Fisica) >>>>>\nInsira seu CPF");
                        dateOfBirth = View.inputDialog("<<<<< Criar Nova Conta Especial (Pessoa Fisica) >>>>>\nInsira sua data de nascimento (formato aaaa-mm-dd)");
                        valueEspecialCheck = View.inputDialogForFloatNumber("<<<<< Criar Nova Conta Especial (Pessoa Fisica) >>>>>\nInsira o valor do cheque especial");
                        newAccount = new SpecialAccount(new PhysicalPerson(ownerName, LocalDate.of(Integer.parseInt(dateOfBirth.split("-")[0].trim()),
                                Integer.parseInt(dateOfBirth.split("-")[1].trim()), Integer.parseInt(dateOfBirth.split("-")[2].trim())), cpf),
                                View.getNewAccountNumber(this.accounts), BankSystem.AGENCY, new BigDecimal(valueEspecialCheck));
                        View.showMessage(String.format("<<<<< Conta Especial/Pessoa Fisica criada com sucesso >>>>>\nNumero da conta: %d\nCheque especial: R$ %s",
                                newAccount.getAccountNumber(), newAccount.getCurrentBalance().toString()));
                        this.accounts.put(newAccount.getAccountNumber(), newAccount);
                    }
                    case 4 -> {
                        String cnpj, dateOfCreation;
                        ownerName = View.inputDialog("<<<<< Criar Nova Conta Especial (Pessoa Juridica) >>>>>\nInsira o nome");
                        cnpj = View.inputDialog("<<<<< Criar Nova Conta Especial (Pessoa Juridica) >>>>>\nInsira o CNPJ");
                        dateOfCreation = View.inputDialog("<<<<< Criar Nova Conta Especial (Pessoa Juridica) >>>>>\nInsira a data de criaçao da empresa/instituiçao (formato aaaa-mm-dd)");
                        valueEspecialCheck = View.inputDialogForFloatNumber("<<<<< Criar Nova Conta Especial (Pessoa Juridica) >>>>>\nInsira o valor do cheque especial");
                        newAccount = new BusinessAccount(new LegalPerson(ownerName, LocalDate.of(Integer.parseInt(dateOfCreation.split("-")[0].trim()),
                                Integer.parseInt(dateOfCreation.split("-")[1].trim()), Integer.parseInt(dateOfCreation.split("-")[2].trim())), cnpj),View.getNewAccountNumber(this.accounts), BankSystem.AGENCY, new BigDecimal(valueEspecialCheck));
                        View.showMessage(String.format("<<<<< Conta Especial/Pessoa Juridica criada com sucesso >>>>>\nNumero da conta: %d\nCheque especial: R$ %s", newAccount.getAccountNumber(), newAccount.getCurrentBalance().toString()));
                        this.accounts.put(newAccount.getAccountNumber(), newAccount);
                    }
                    case 5 -> {
                        return;
                    }
                    default -> View.showMessage("<<<<< Escolha uma opcao valida >>>>>");
                }
            } catch (NumberFormatException | NullPointerException exc) {
                View.showMessage("<<<<< Entrada Invalida >>>>>");
            } catch (ArrayIndexOutOfBoundsException exc) {
                View.showMessage("<<<<< Verifique a data fornecida e tente novamente >>>>>");
            }
        }
    }

    private void informationOfAnAccount() {
        try {
            if (this.accounts.isEmpty()) {
                View.showMessage("<<<<< Nenhuma conta foi cadastrada >>>>>");
                return;
            }
            int numberOfAccount = View.inputDialogForIntegerNumber("<<<<< Informacoes sobre conta >>>>>\nInsira"
                    + " o numero da conta");
            if (!this.accounts.containsKey(numberOfAccount)) throw new AccountNotFoundException();
            Account requiredAccount = this.accounts.get(numberOfAccount);
            View.showMessage("<<<<< Informacoes sobre conta >>>>>\n" + requiredAccount.getStandardized());
        } catch (AccountNotFoundException exception) {
            View.showMessage(exception.getMessage());
        } catch (NumberFormatException exception) {
            View.showMessage("<<<<< Entrada invalida >>>>>");
        }
    }

    private void movementAccount() throws FileNotFoundException {
        if (this.accounts.isEmpty()) {
            View.showMessage("<<<<< Nenhuma conta foi cadastrada >>>>>");
            return;
        }
        while (true) {
            try {
                switch (View.menu(this.menus.getString("accountMoveMenu"))) {
                    case 1 -> withDrawMoneyOfAnAccount();
                    case 2 -> depositMoneyInAnAccount();
                    case 3 -> transferMoneyBetweenAccounts();
                    case 4 -> { return; }
                    default -> View.showMessage("<<<<< Escolha uma opcao valida >>>>>");
                }
            } catch (NumberFormatException exc) {
                View.showMessage("<<<<< Entrada invalida >>>>>");
            } catch (AccountNotFoundException | InsufficientFundsException | IllegalArgumentException exception) {
                View.showMessage(exception.getMessage());
            }
        }
    }

    private void withDrawMoneyOfAnAccount() throws AccountNotFoundException, IllegalArgumentException, InsufficientFundsException {
        int numberOfAccount = View.inputDialogForIntegerNumber("<<<<< Sacar quantia de uma conta >>>>>\n"
                + "Insira o numero da conta");
        if (!this.accounts.containsKey(numberOfAccount)) throw new AccountNotFoundException();
        Account requiredAccount = this.accounts.get(numberOfAccount);
        String amountToGet = View.inputDialog(String.format("<<<<< Sacar quantia de uma conta >>>>>%n"
                + "%s%nInsira a quantia a sacar", requiredAccount.getStandardized()));
        requiredAccount.withdraw(new BigDecimal(amountToGet)); //This line can throw an InsufficientFundsException or an IllegalArgumentException!
        View.showMessage(String.format("<<<<< Sacar quantia de uma conta >>>>>%nSaque efetuado com sucesso!%n" +
                "Saldo atual da conta: R$ %s", requiredAccount.getCurrentBalance().toString())); //If everything went as expected, we'll get here!
    }

    private void depositMoneyInAnAccount() throws AccountNotFoundException, IllegalArgumentException {
        int numberOfAccount = View.inputDialogForIntegerNumber("<<<<< Depositar quantia em uma conta >>>>>\nInsira o numero da conta");
        if (!this.accounts.containsKey(numberOfAccount)) throw new AccountNotFoundException();
        Account requiredAccount = this.accounts.get(numberOfAccount);
        String amountToDeposit = View.inputDialog(String.format("<<<<< Depositar quantia em uma conta >>>>>%n%s%nInsira a quantia a depositar", requiredAccount.getStandardized()));
        requiredAccount.deposit(new BigDecimal(amountToDeposit)); //This line can throw an IllegalArgumentException
        View.showMessage(String.format("<<<<< Depositar quantia em uma conta >>>>>%nDeposito efetuado com sucesso!%nSaldo atual da conta: R$ %s", requiredAccount.getCurrentBalance().toString()));
    }

    private void transferMoneyBetweenAccounts() throws AccountNotFoundException, InsufficientFundsException, IllegalArgumentException {
        if (this.accounts.size() <= 1) {
            View.showMessage("<<<<< Pelo menos duas contas devem ja ter sido cadastradas >>>>>");
            return;
        }
        int numberOfAccountToTransferWith = View.inputDialogForIntegerNumber(String.format("<<<<< Transferir"
                + " de uma conta para outra >>>>>%nInsira o numero da conta depositante"));
        if (!this.accounts.containsKey(numberOfAccountToTransferWith)) throw new AccountNotFoundException();
        int numberOfAccountToReceiveAmount = View.inputDialogForIntegerNumber(String.format("<<<<< Transferir"
                + " de uma conta para outra >>>>>%nInsira o numero da conta receptora"));
        if (!this.accounts.containsKey(numberOfAccountToReceiveAmount)) throw new AccountNotFoundException();
        Account toTransferWith = this.accounts.get(numberOfAccountToTransferWith);
        Account toReceiveAmount = this.accounts.get(numberOfAccountToReceiveAmount);
        String amountToTransfer = View.inputDialog(String.format("<<<<< Transferir de uma conta para outra >>>>>%n%n> " +
                "Conta depositante <%n%s%n> Conta receptora <%n%s%nInsira a quantia a transferir", toTransferWith.getStandardized(),toReceiveAmount.getStandardized()));
        toTransferWith.transfer(toReceiveAmount, new BigDecimal(amountToTransfer));
        View.showMessage(String.format("<<<<< Transferencia realizada com sucesso! >>>>>%nConta depositante: saldo atual " +
                "(R$ %s)%nConta receptora: saldo atual (R$ %s)", toTransferWith.getCurrentBalance().toString(), toReceiveAmount.getCurrentBalance().toString()));
    }
}
