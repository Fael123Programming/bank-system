package br.com.rafael.extra.bank;

import br.com.rafael.extra.accounts.owners.concrete.*;
import br.com.rafael.extra.accounts.types._abstract.Account;
import br.com.rafael.extra.accounts.types.concrete.*;
import br.com.rafael.extra.exceptions.*;
import br.com.rafael.extra.manager.Manager;
import br.com.rafael.extra.persistence.FileHandler;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class BankSystem {
    private static final String AGENCY = "MAIN-AGENCY";
    private final Map<Integer, Account> accounts;
    private final String path;

    public BankSystem(String filePath) throws UnparseableStringAccount {
        Manager manager = Manager.getInstance();
        if (!FileHandler.fileExists(filePath)) {
            File file = new File(filePath);
            try {
                file.createNewFile();
                //If filePath already exists, the method above will return false and no file will be created.
                //If not, a new file with path 'filePath' will be created in this system's files.
            } catch (IOException exc) {
                manager.showMessage("<<<<< Infelizmente, um problema ocorreu na criacao do arquivo para registrar as contas! >>>>>");
                manager.exit();
            }
        }
        this.path = filePath;
        this.accounts = new HashMap<>();
        Account account;
        //Bringing all accounts back from the file to the attribute 'accounts'.
        try {
            Scanner fileScan = new Scanner(new File(filePath));
            while (fileScan.hasNextLine()) {
                account = Account.parseString(fileScan.nextLine());
                this.accounts.put(account.getAccountNumber(), account);
            }
        } catch(FileNotFoundException exc) {
            //Yes... nothing should be done here. Actually, this is never going to be reached due line 23.
        } catch (UnparseableStringAccount exc) {
            manager.showMessage("<<<<< Os dados do arquivo de contas estao corrompidos ou fora do padrao pre-definido >>>>>");
            System.exit(-1);
        }
    }

    public void start() {
        Manager manager = Manager.getInstance();
        while (true) {
            try {
                switch (manager.menu("""
                        <<<<< Sistema de Banco 2.0.0 >>>>>
                        1. Criar nova conta
                        2. Exibir informacoes de uma conta
                        3. Movimentar conta
                        4. Sair do sistema""")) {
                    case 1 -> createAccount();
                    case 2 -> informationOfAnAccount();
                    case 3 -> movementAccount();//Here withdraw and deposit methods were implemented
                    case 4 -> {
                        //Recording all accounts back into the file.
                        FileHandler.clean(this.path);
                        for (Account account : this.accounts.values()) {
                            FileHandler.appendTo(this.path, account.toString());
                        }
                        manager.exit();
                    }
                    default -> manager.showMessage("<<<<< Escolha uma opcao valida >>>>>");
                }
            } catch (NumberFormatException exc) {
                manager.showMessage("<<<<< Escolha uma opcao valida >>>>>");
            } catch (FileNotFoundException exc) {
                //Yes... nothing should be done here. Actually, this is never going to be reached due line 23.
            } catch (IOException exc) {
                manager.showMessage("<<<<< Um erro ocorreu ao tentar registrar as contas no arquivo de contas >>>>>");
            }
        }
    }

    private void createAccount() {
        Manager manager = Manager.getInstance();
        Account newAccount;
        String ownerName, cpf, dateOfBirth;
        int typeOfAccount;
        double valueEspecialCheck;
        while (true) {
            try {
                typeOfAccount = manager.menu("""
                        <<<<< Criar Nova Conta >>>>>
                        1. Conta Corrente\040\040\040
                        2. Conta Poupanca
                        3. Conta Especial (Pessoa Fisica)
                        4. Conta Especial (Pessoa Juridica)
                        5. Voltar ao menu principal""");
                switch (typeOfAccount) {
                    case 1 -> {
                        ownerName = manager.inputDialog("<<<<< Criar Nova Conta Corrente >>>>>\nInsira seu nome");
                        cpf = manager.inputDialog("<<<<< Criar Nova Conta Corrente >>>>>\nInsira seu CPF");
                        dateOfBirth = manager.inputDialog("<<<<< Criar Nova Conta Corrente >>>>>\nInsira sua data de nascimento (formato aaaa-mm-dd)");
                        newAccount = new CurrentAccount(new PhysicalPerson(ownerName, LocalDate.of(Integer.parseInt(dateOfBirth.split("-")[0].trim()),
                                Integer.parseInt(dateOfBirth.split("-")[1].trim()), Integer.parseInt(dateOfBirth.split("-")[2].trim())), cpf), manager.getNewAccountNumber(this.accounts), BankSystem.AGENCY);
                        manager.showMessage(String.format("<<<<< Conta Corrente criada com sucesso >>>>>\n Numero da conta: %d", newAccount.getAccountNumber()));
                        this.accounts.put(newAccount.getAccountNumber(), newAccount);
                    }
                    case 2 -> {
                        ownerName = manager.inputDialog("<<<<< Criar Nova Conta Poupança >>>>>\nInsira seu nome");
                        cpf = manager.inputDialog("<<<<< Criar Nova Conta Poupança >>>>>\nInsira seu CPF");
                        dateOfBirth = manager.inputDialog("<<<<< Criar Nova Conta Poupança >>>>>\nInsira sua data de nascimento (formato aaaa-mm-dd)");
                        newAccount = new SavingsAccount(new PhysicalPerson(ownerName, LocalDate.of(Integer.parseInt(dateOfBirth.split("-")[0].trim()),
                                Integer.parseInt(dateOfBirth.split("-")[1].trim()), Integer.parseInt(dateOfBirth.split("-")[2].trim())), cpf), manager.getNewAccountNumber(this.accounts), BankSystem.AGENCY);
                        manager.showMessage(String.format("<<<<< Conta Poupanca criada com sucesso >>>>>\nNumero da conta: %d\nRendimento: %s%%", newAccount.getAccountNumber(), SavingsAccount.getYieldPercentage().movePointRight(2)));
                        this.accounts.put(newAccount.getAccountNumber(), newAccount);
                    }
                    case 3 -> {
                        ownerName = manager.inputDialog("<<<<< Criar Nova Conta Especial (Pessoa Fisica) >>>>>\nInsira seu nome");
                        cpf = manager.inputDialog("<<<<< Criar Nova Conta Especial (Pessoa Fisica) >>>>>\nInsira seu CPF");
                        dateOfBirth = manager.inputDialog("<<<<< Criar Nova Conta Especial (Pessoa Fisica) >>>>>\nInsira sua data de nascimento (formato aaaa-mm-dd)");
                        valueEspecialCheck = manager.inputDialogForFloatNumber("<<<<< Criar Nova Conta Especial (Pessoa Fisica) >>>>>\nInsira o valor do cheque especial");
                        newAccount = new EspecialAccount(new PhysicalPerson(ownerName, LocalDate.of(Integer.parseInt(dateOfBirth.split("-")[0].trim()),
                                Integer.parseInt(dateOfBirth.split("-")[1].trim()), Integer.parseInt(dateOfBirth.split("-")[2].trim())), cpf),
                                manager.getNewAccountNumber(this.accounts), BankSystem.AGENCY, new BigDecimal(valueEspecialCheck));
                        manager.showMessage(String.format("<<<<< Conta Especial/Pessoa Fisica criada com sucesso >>>>>\nNumero da conta: %d\nCheque especial: R$ %s",
                                newAccount.getAccountNumber(), newAccount.getCurrentBalance().toString()));
                        this.accounts.put(newAccount.getAccountNumber(), newAccount);
                    }
                    case 4 -> {
                        String cnpj, dateOfCreation;
                        ownerName = manager.inputDialog("<<<<< Criar Nova Conta Especial (Pessoa Juridica) >>>>>\nInsira o nome");
                        cnpj = manager.inputDialog("<<<<< Criar Nova Conta Especial (Pessoa Juridica) >>>>>\nInsira o CNPJ");
                        dateOfCreation = manager.inputDialog("<<<<< Criar Nova Conta Especial (Pessoa Juridica) >>>>>\nInsira a data de criaçao da empresa/instituiçao (formato aaaa-mm-dd)");
                        valueEspecialCheck = manager.inputDialogForFloatNumber("<<<<< Criar Nova Conta Especial (Pessoa Juridica) >>>>>\nInsira o valor do cheque especial");
                        newAccount = new BusinessAccount(new LegalPerson(ownerName, LocalDate.of(Integer.parseInt(dateOfCreation.split("-")[0].trim()),
                                Integer.parseInt(dateOfCreation.split("-")[1].trim()), Integer.parseInt(dateOfCreation.split("-")[2].trim())), cnpj),manager.getNewAccountNumber(this.accounts), BankSystem.AGENCY, new BigDecimal(valueEspecialCheck));
                        manager.showMessage(String.format("<<<<< Conta Especial/Pessoa Juridica criada com sucesso >>>>>\nNumero da conta: %d\nCheque especial: R$ %s", newAccount.getAccountNumber(), newAccount.getCurrentBalance().toString()));
                        this.accounts.put(newAccount.getAccountNumber(), newAccount);
                    }
                    case 5 -> {
                        return;
                    }
                    default -> manager.showMessage("<<<<< Escolha uma opcao valida >>>>>");
                }
            } catch (NumberFormatException | NullPointerException exc) {
                manager.showMessage("<<<<< Entrada Invalida >>>>>");
            } catch (ArrayIndexOutOfBoundsException exc) {
                manager.showMessage("<<<<< Verifique a data fornecida e tente novamente >>>>>");
            }
        }
    }

    private void informationOfAnAccount() {
        Manager manager = Manager.getInstance();
        try {
            if (this.accounts.isEmpty()) {
                manager.showMessage("<<<<< Nenhuma conta foi cadastrada >>>>>");
                return;
            }
            int numberOfAccount = manager.inputDialogForIntegerNumber("<<<<< Informacoes sobre conta >>>>>\nInsira"
                    + " o numero da conta");
            if (!this.accounts.containsKey(numberOfAccount)) throw new AccountNotFoundException();
            Account requiredAccount = this.accounts.get(numberOfAccount);
            manager.showMessage("<<<<< Informacoes sobre conta >>>>>\n" + requiredAccount.getStandardized());
        } catch (AccountNotFoundException exception) {
            manager.showMessage(exception.getMessage());
        } catch (NumberFormatException exception) {
            manager.showMessage("<<<<< Entrada invalida >>>>>");
        }
    }

    private void movementAccount() throws FileNotFoundException {
        Manager manager = Manager.getInstance();
        if (this.accounts.isEmpty()) {
            manager.showMessage("<<<<< Nenhuma conta foi cadastrada >>>>>");
            return;
        }
        while (true) {
            try {
                switch (manager.menu("""
                    <<<<< Movimentar conta >>>>>
                    1. Sacar quantia
                    2. Depositar quantia
                    3. Transferir de uma conta para outra
                    4. Voltar ao menu principal""")) {
                    case 1 -> withDrawMoneyOfAnAccount();
                    case 2 -> depositMoneyInAnAccount();
                    case 3 -> transferMoneyBetweenAccounts();
                    case 4 -> { return; }
                    default -> manager.showMessage("<<<<< Escolha uma opcao valida >>>>>");
                }
            } catch (NumberFormatException exc) {
                manager.showMessage("<<<<< Entrada invalida >>>>>");
            } catch (AccountNotFoundException | InsufficientFundsException | IllegalArgumentException exception) {
                manager.showMessage(exception.getMessage());
            }
        }
    }

    private void withDrawMoneyOfAnAccount() throws AccountNotFoundException, IllegalArgumentException, InsufficientFundsException {
        Manager manager = Manager.getInstance();
        int numberOfAccount = manager.inputDialogForIntegerNumber("<<<<< Sacar quantia de uma conta >>>>>\n"
                + "Insira o numero da conta");
        if (!this.accounts.containsKey(numberOfAccount)) throw new AccountNotFoundException();
        Account requiredAccount = this.accounts.get(numberOfAccount);
        String amountToGet = manager.inputDialog(String.format("<<<<< Sacar quantia de uma conta >>>>>%n"
                + "%s%nInsira a quantia a sacar", requiredAccount.getStandardized()));
        requiredAccount.withdraw(new BigDecimal(amountToGet)); //This line can throw an InsufficientFundsException or an IllegalArgumentException!
        manager.showMessage(String.format("<<<<< Sacar quantia de uma conta >>>>>%nSaque efetuado com sucesso!%n" +
                "Saldo atual da conta: R$ %s", requiredAccount.getCurrentBalance().toString())); //If everything went as expected, we'll get here!
    }

    private void depositMoneyInAnAccount() throws AccountNotFoundException, IllegalArgumentException {
        Manager manager = Manager.getInstance();
        int numberOfAccount = manager.inputDialogForIntegerNumber("<<<<< Depositar quantia em uma conta >>>>>\nInsira o numero da conta");
        if (!this.accounts.containsKey(numberOfAccount)) throw new AccountNotFoundException();
        Account requiredAccount = this.accounts.get(numberOfAccount);
        String amountToDeposit = manager.inputDialog(String.format("<<<<< Depositar quantia em uma conta >>>>>%n%s%nInsira a quantia a depositar", requiredAccount.getStandardized()));
        requiredAccount.deposit(new BigDecimal(amountToDeposit)); //This line can throw an IllegalArgumentException
        manager.showMessage(String.format("<<<<< Depositar quantia em uma conta >>>>>%nDeposito efetuado com sucesso!%nSaldo atual da conta: R$ %s", requiredAccount.getCurrentBalance().toString()));
    }

    private void transferMoneyBetweenAccounts() throws AccountNotFoundException, InsufficientFundsException, IllegalArgumentException {
        Manager manager = Manager.getInstance();
        if (this.accounts.size() <= 1) {
            manager.showMessage("<<<<< Pelo menos duas contas devem ja ter sido cadastradas >>>>>");
            return;
        }
        int numberOfAccountToTransferWith = manager.inputDialogForIntegerNumber(String.format("<<<<< Transferir"
                + " de uma conta para outra >>>>>%nInsira o numero da conta depositante"));
        if (!this.accounts.containsKey(numberOfAccountToTransferWith)) throw new AccountNotFoundException();
        int numberOfAccountToReceiveAmount = manager.inputDialogForIntegerNumber(String.format("<<<<< Transferir"
                + " de uma conta para outra >>>>>%nInsira o numero da conta receptora"));
        if (!this.accounts.containsKey(numberOfAccountToReceiveAmount)) throw new AccountNotFoundException();
        Account toTransferWith = this.accounts.get(numberOfAccountToTransferWith);
        Account toReceiveAmount = this.accounts.get(numberOfAccountToReceiveAmount);
        String amountToTransfer = manager.inputDialog(String.format("<<<<< Transferir de uma conta para outra >>>>>%n%n> " +
                "Conta depositante <%n%s%n> Conta receptora <%n%s%nInsira a quantia a transferir", toTransferWith.getStandardized(),toReceiveAmount.getStandardized()));
        toTransferWith.transfer(toReceiveAmount, new BigDecimal(amountToTransfer));
        manager.showMessage(String.format("<<<<< Transferencia realizada com sucesso! >>>>>%nConta depositante: saldo atual " +
                "(R$ %s)%nConta receptora: saldo atual (R$ %s)", toTransferWith.getCurrentBalance().toString(), toReceiveAmount.getCurrentBalance().toString()));
    }
}
