package com.douglasog87.service;

import com.douglasog87.exceptions.MissingEnvironmentVariableException;
import com.douglasog87.model.Client;
import com.douglasog87.model.Sale;
import com.douglasog87.model.SalesMan;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Log4j2
@Component
public class ProcessFiles {

    public static final Map<String, String> env = System.getenv();
    public static final String SALESMAN_PREFIX = "001";
    public static final String CLIENT_PREFIX = "002";
    public static final String SALE_PREFIX = "003";
    private static final int INTERVAL = 30000;

    @Value("${environmentHomePath:HOME}")
    private String ENVIRONMENT_HOME_PATH;
    @Value("${path.in:/data/in}")
    private String DATA_IN;
    @Value("${path.out:/data/out}")
    private String DATA_OUT;

    private List<SalesMan> saleManList;
    private List<Client> clientList;
    private List<Sale> saleList;
    private StringBuilder info;

    @PostConstruct
    public void checkAndCreatePaths() throws Exception {
        log.info("===> checkAndCreatePaths");
        String path = env.getOrDefault(ENVIRONMENT_HOME_PATH, "~/");
        if (Objects.isNull(path)) throw new MissingEnvironmentVariableException(ENVIRONMENT_HOME_PATH);
        final List<File> folders = new ArrayList<>();
        folders.add(new File(path + DATA_IN));
        folders.add(new File(path + DATA_OUT));
        folders.parallelStream().forEach(folder -> folder.mkdirs());
    }

    private List<File> getFiles(String path) {
        File folder = new File(path);
        return Arrays.asList(folder.listFiles(pathname -> pathname.isFile() && pathname.canRead() && pathname.getName().contains(".dat")));
    }

    @Scheduled(fixedDelay = INTERVAL)
    private void runner() {
        info = new StringBuilder();
        info.append("\n===> running process files\n");
        final List<File> fileListIN = getFiles(env.getOrDefault(ENVIRONMENT_HOME_PATH, "~/").concat(DATA_IN));

        if (Objects.nonNull(fileListIN)) {
            fileListIN.stream().forEach(file -> {
                saleManList = new ArrayList<>();
                clientList = new ArrayList<>();
                saleList = new ArrayList<>();

                File fileOUT = new File(env.getOrDefault(ENVIRONMENT_HOME_PATH, "~/").concat(DATA_OUT).concat("/").concat(file.getName()).replace(".dat", ".done.dat"));

                if (!fileOUT.exists()) {
                    info.append("     ===> processing file " + file.getName() + "\n");
                    readFile(file);
                    resume(fileOUT);
                } else {
                    info.append("     ===> file " + file.getName() + " already processed\n");
                }
            });
        }

        log.info(info.toString());
    }

    private void readFile(File file) {
        try {
            List<String> lines = FileUtils.readLines(file, "UTF-8");

            lines.parallelStream().forEach(line -> {
                String[] lineData = line.split("ç");
                switch (lineData[0]) {
                    case SALESMAN_PREFIX:
                        saleManList.add(SalesMan.builder()
                                .cpf(lineData[1])
                                .name(lineData[2])
                                .salary(Double.parseDouble(lineData[3]))
                                .build());
                        break;
                    case CLIENT_PREFIX:
                        clientList.add(Client.builder()
                                .cnpj(lineData[1])
                                .name(lineData[2])
                                .businessArea(lineData[3])
                                .build());
                        break;
                    case SALE_PREFIX:
                        Sale sale = Sale.builder()
                                .saleId(Long.parseLong(lineData[1]))
                                .itens(Sale.parseItens(lineData[2].split(",")))
                                .salesMan(lineData[3])
                                .build();
                        sale.calculeTotal();
                        saleList.add(sale);
                        break;
                }
            });
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void resume(File fileOUT) {
        try {
            info.append("     ===> resume to file " + fileOUT.getName() + "\n");
            if (fileOUT.createNewFile()) {

                Sale bestSale = saleList.parallelStream().max(Comparator.comparing(sale -> sale.getTotal())).get();
                Sale worstSale = saleList.parallelStream().min(Comparator.comparing(sale -> sale.getTotal())).get();

                BufferedWriter writer = new BufferedWriter(new FileWriter(fileOUT));
                writer.write("clients quantity: ".concat(String.valueOf(clientList.size())));
                writer.newLine();
                writer.write("salesMan quantity: ".concat(String.valueOf(saleManList.size())));
                writer.newLine();
                writer.write("most expensive saleId: ".concat(String.valueOf(bestSale.getSaleId())));
                writer.newLine();
                writer.write("worst seller: ".concat(worstSale.getSalesMan()));
                writer.newLine();

                writer.close();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }


    }

}
