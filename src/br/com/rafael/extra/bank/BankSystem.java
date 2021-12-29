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
    private final Map<Integer, Account> ACCOUNTS;
    private final String ACCOUNT_FILE_PATH;
    private final ResourceBundle BUNDLE;

    public BankSystem(String filePath, Locale baseLocale) {
        if (!FileHandler.fileExists(filePath)) {
            File file = new File(filePath);
            try {
                file.createNewFile();
                //If filePath already exists, the method above will return false and no file will be created.
                //If not, a new file with path 'filePath' will be created in this system's files.
            } catch (IOException exc) {
                View.showMessage("<<<<< Infelizmente, um problema ocorreu na criacao do arquivo para registrar as contas! >>>>>");
                View.exit();
            }
        }
        this.BUNDLE = ResourceBundle.getBundle("br.com.rafael.resourcebundles.menus", baseLocale);
        this.ACCOUNT_FILE_PATH = filePath;
        this.ACCOUNTS = new HashMap<>();
        Account account;
        //Bringing all accounts back from the file to the attribute 'accounts'.
        try {
            Scanner fileScan = new Scanner(new File(filePath));
            while (fileScan.hasNextLine()) {
                account = Account.parseString(fileScan.nextLine());
                this.ACCOUNTS.put(account.getAccountNumber(), account);
            }
        } catch(FileNotFoundException exc) {
            //Yes... nothing should be done here. Actually, this is never going to be reached due line 27.
        } catch (UnparseableStringAccount exc) {
            View.showMessage("<<<<< Os dados do arquivo de contas estao corrompidos ou fora do padrao pre-definido >>>>>");
            System.exit(-1);
        }
    }

    public void start() {
        while (true) {
            try {
                switch (View.menu(this.BUNDLE.getString("mainMenu"))) {
                    case 1 -> createAccount();
                    case 2 -> informationOfAnAccount();
                    case 3 -> movementAccount();//Here withdraw and deposit methods were implemented
                    case 4 -> {
                        //Recording all accounts back into the file.
                        FileHandler.clean(this.ACCOUNT_FILE_PATH);
                        for (Account account : this.ACCOUNTS.values())
                            FileHandler.appendTo(this.ACCOUNT_FILE_PATH, account.toString());
                        View.exit();
                    }
                    default -> View.showMessage("<<<<< Escolha uma opcao valida >>>>>");
                }
            } catch (NumberFormatException exc) {
                View.showMessage("<<<<< Escolha uma opcao valida >>>>>");
            } catch (FileNotFoundException exc) {
                //Yes... nothing should be done here. Actually, this is never going to be reached due line 23.
            } catch (IOException exc) {
                View.showMessage("<<<<< Um erro ocorreu ao tentar registrar as contas no arquivo de contas >>>>>");
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
                typeOfAccount = View.menu(this.BUNDLE.getString("accountCreationMenu"));
                switch (typeOfAccount) {
                    case 1 -> {
                        ownerName = View.inputDialog("<<<<< Criar Nova Conta Corrente >>>>>\nInsira seu nome");
                        cpf = View.inputDialog("<<<<< Criar Nova Conta Corrente >>>>>\nInsira seu CPF");
                        dateOfBirth = View.inputDialog("<<<<< Criar Nova Conta Corrente >>>>>\nInsira sua data de nascimento (formato aaaa-mm-dd)");
                        newAccount = new CurrentAccount(new PhysicalPerson(ownerName, LocalDate.of(Integer.parseInt(dateOfBirth.split("-")[0].trim()),
                                Integer.parseInt(dateOfBirth.split("-")[1].trim()), Integer.parseInt(dateOfBirth.split("-")[2].trim())), cpf), View.getNewAccountNumber(this.ACCOUNTS), BankSystem.AGENCY);
                        View.showMessage(String.format("<<<<< Conta Corrente criada com sucesso >>>>>\n Numero da conta: %d", newAccount.getAccountNumber()));
                        this.ACCOUNTS.put(newAccount.getAccountNumber(), newAccount);
                    }
                    case 2 -> {
                        ownerName = View.inputDialog("<<<<< Criar Nova Conta Poupança >>>>>\nInsira seu nome");
                        cpf = View.inputDialog("<<<<< Criar Nova Conta Poupança >>>>>\nInsira seu CPF");
                        dateOfBirth = View.inputDialog("<<<<< Criar Nova Conta Poupança >>>>>\nInsira sua data de nascimento (formato aaaa-mm-dd)");
                        newAccount = new SavingsAccount(new PhysicalPerson(ownerName, LocalDate.of(Integer.parseInt(dateOfBirth.split("-")[0].trim()),
                                Integer.parseInt(dateOfBirth.split("-")[1].trim()), Integer.parseInt(dateOfBirth.split("-")[2].trim())), cpf), View.getNewAccountNumber(this.ACCOUNTS), BankSystem.AGENCY);
                        View.showMessage(String.format("<<<<< Conta Poupanca criada com sucesso >>>>>\nNumero da conta: %d\nRendimento: %s%%", newAccount.getAccountNumber(), SavingsAccount.getYieldPercentage().movePointRight(2)));
                        this.ACCOUNTS.put(newAccount.getAccountNumber(), newAccount);
                    }
                    case 3 -> {
                        ownerName = View.inputDialog("<<<<< Criar Nova Conta Especial (Pessoa Fisica) >>>>>\nInsira seu nome");
                        cpf = View.inputDialog("<<<<< Criar Nova Conta Especial (Pessoa Fisica) >>>>>\nInsira seu CPF");
                        dateOfBirth = View.inputDialog("<<<<< Criar Nova Conta Especial (Pessoa Fisica) >>>>>\nInsira sua data de nascimento (formato aaaa-mm-dd)");
                        valueEspecialCheck = View.inputDialogForFloatNumber("<<<<< Criar Nova Conta Especial (Pessoa Fisica) >>>>>\nInsira o valor do cheque especial");
                        newAccount = new SpecialAccount(new PhysicalPerson(ownerName, LocalDate.of(Integer.parseInt(dateOfBirth.split("-")[0].trim()),
                                Integer.parseInt(dateOfBirth.split("-")[1].trim()), Integer.parseInt(dateOfBirth.split("-")[2].trim())), cpf),
                                View.getNewAccountNumber(this.ACCOUNTS), BankSystem.AGENCY, new BigDecimal(valueEspecialCheck));
                        View.showMessage(String.format("<<<<< Conta Especial/Pessoa Fisica criada com sucesso >>>>>\nNumero da conta: %d\nCheque especial: R$ %s",
                                newAccount.getAccountNumber(), newAccount.getCurrentBalance().toString()));
                        this.ACCOUNTS.put(newAccount.getAccountNumber(), newAccount);
                    }
                    case 4 -> {
                        String cnpj, dateOfCreation;
                        ownerName = View.inputDialog("<<<<< Criar Nova Conta Especial (Pessoa Juridica) >>>>>\nInsira o nome");
                        cnpj = View.inputDialog("<<<<< Criar Nova Conta Especial (Pessoa Juridica) >>>>>\nInsira o CNPJ");
                        dateOfCreation = View.inputDialog("<<<<< Criar Nova Conta Especial (Pessoa Juridica) >>>>>\nInsira a data de criaçao da empresa/instituiçao (formato aaaa-mm-dd)");
                        valueEspecialCheck = View.inputDialogForFloatNumber("<<<<< Criar Nova Conta Especial (Pessoa Juridica) >>>>>\nInsira o valor do cheque especial");
                        newAccount = new BusinessAccount(new LegalPerson(ownerName, LocalDate.of(Integer.parseInt(dateOfCreation.split("-")[0].trim()),
                                Integer.parseInt(dateOfCreation.split("-")[1].trim()), Integer.parseInt(dateOfCreation.split("-")[2].trim())), cnpj),View.getNewAccountNumber(this.ACCOUNTS), BankSystem.AGENCY, new BigDecimal(valueEspecialCheck));
                        View.showMessage(String.format("<<<<< Conta Especial/Pessoa Juridica criada com sucesso >>>>>\nNumero da conta: %d\nCheque especial: R$ %s", newAccount.getAccountNumber(), newAccount.getCurrentBalance().toString()));
                        this.ACCOUNTS.put(newAccount.getAccountNumber(), newAccount);
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
            if (this.ACCOUNTS.isEmpty()) {
                View.showMessage("<<<<< Nenhuma conta foi cadastrada >>>>>");
                return;
            }
            int numberOfAccount = View.inputDialogForIntegerNumber("<<<<< Informacoes sobre conta >>>>>\nInsira"
                    + " o numero da conta");
            if (!this.ACCOUNTS.containsKey(numberOfAccount)) throw new AccountNotFoundException();
            Account requiredAccount = this.ACCOUNTS.get(numberOfAccount);
            View.showMessage("<<<<< Informacoes sobre conta >>>>>\n" + requiredAccount.getStandardized());
        } catch (AccountNotFoundException exception) {
            View.showMessage(exception.getMessage());
        } catch (NumberFormatException exception) {
            View.showMessage("<<<<< Entrada invalida >>>>>");
        }
    }

    private void movementAccount() throws FileNotFoundException {
        if (this.ACCOUNTS.isEmpty()) {
            View.showMessage("<<<<< Nenhuma conta foi cadastrada >>>>>");
            return;
        }
        while (true) {
            try {
                switch (View.menu(this.BUNDLE.getString("accountMoveMenu"))) {
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
        if (!this.ACCOUNTS.containsKey(numberOfAccount)) throw new AccountNotFoundException();
        Account requiredAccount = this.ACCOUNTS.get(numberOfAccount);
        String amountToGet = View.inputDialog(String.format("<<<<< Sacar quantia de uma conta >>>>>%n"
                + "%s%nInsira a quantia a sacar", requiredAccount.getStandardized()));
        requiredAccount.withdraw(new BigDecimal(amountToGet)); //This line can throw an InsufficientFundsException or an IllegalArgumentException!
        View.showMessage(String.format("<<<<< Sacar quantia de uma conta >>>>>%nSaque efetuado com sucesso!%n" +
                "Saldo atual da conta: R$ %s", requiredAccount.getCurrentBalance().toString())); //If everything went as expected, we'll get here!
    }

    private void depositMoneyInAnAccount() throws AccountNotFoundException, IllegalArgumentException {
        int numberOfAccount = View.inputDialogForIntegerNumber("<<<<< Depositar quantia em uma conta >>>>>\nInsira o numero da conta");
        if (!this.ACCOUNTS.containsKey(numberOfAccount)) throw new AccountNotFoundException();
        Account requiredAccount = this.ACCOUNTS.get(numberOfAccount);
        String amountToDeposit = View.inputDialog(String.format("<<<<< Depositar quantia em uma conta >>>>>%n%s%nInsira a quantia a depositar", requiredAccount.getStandardized()));
        requiredAccount.deposit(new BigDecimal(amountToDeposit)); //This line can throw an IllegalArgumentException
        View.showMessage(String.format("<<<<< Depositar quantia em uma conta >>>>>%nDeposito efetuado com sucesso!%nSaldo atual da conta: R$ %s", requiredAccount.getCurrentBalance().toString()));
    }

    private void transferMoneyBetweenAccounts() throws AccountNotFoundException, InsufficientFundsException, IllegalArgumentException {
        if (this.ACCOUNTS.size() <= 1) {
            View.showMessage("<<<<< Pelo menos duas contas devem ja ter sido cadastradas >>>>>");
            return;
        }
        int numberOfAccountToTransferWith = View.inputDialogForIntegerNumber(String.format("<<<<< Transferir"
                + " de uma conta para outra >>>>>%nInsira o numero da conta depositante"));
        if (!this.ACCOUNTS.containsKey(numberOfAccountToTransferWith)) throw new AccountNotFoundException();
        int numberOfAccountToReceiveAmount = View.inputDialogForIntegerNumber(String.format("<<<<< Transferir"
                + " de uma conta para outra >>>>>%nInsira o numero da conta receptora"));
        if (!this.ACCOUNTS.containsKey(numberOfAccountToReceiveAmount)) throw new AccountNotFoundException();
        Account toTransferWith = this.ACCOUNTS.get(numberOfAccountToTransferWith);
        Account toReceiveAmount = this.ACCOUNTS.get(numberOfAccountToReceiveAmount);
        String amountToTransfer = View.inputDialog(String.format("<<<<< Transferir de uma conta para outra >>>>>%n%n> " +
                "Conta depositante <%n%s%n> Conta receptora <%n%s%nInsira a quantia a transferir", toTransferWith.getStandardized(),toReceiveAmount.getStandardized()));
        toTransferWith.transfer(toReceiveAmount, new BigDecimal(amountToTransfer));
        View.showMessage(String.format("<<<<< Transferencia realizada com sucesso! >>>>>%nConta depositante: saldo atual " +
                "(R$ %s)%nConta receptora: saldo atual (R$ %s)", toTransferWith.getCurrentBalance().toString(), toReceiveAmount.getCurrentBalance().toString()));
    }
}
