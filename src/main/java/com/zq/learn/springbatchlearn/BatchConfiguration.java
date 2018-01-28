package com.zq.learn.springbatchlearn;

import com.zq.learn.springbatchlearn.dto.Employee;
import com.zq.learn.springbatchlearn.dto.ParsedItem;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.support.ListItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private static final String[] columns = StringUtils.commaDelimitedListToStringArray(
            "a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z"
    );

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Bean
    public ItemReader<ParsedItem> csvFileItemReader() {
        FlatFileItemReader<ParsedItem> csvFileReader = new FlatFileItemReader<>();
        csvFileReader.setResource(new ClassPathResource("data/employee.csv"));
        csvFileReader.setLinesToSkip(1);

        csvFileReader.setLineMapper(new DefaultLineMapper<ParsedItem>() {{
            setLineTokenizer(new DelimitedLineTokenizer());
            setFieldSetMapper(fieldSet -> {
                ParsedItem parseItem = new ParsedItem();
                String[] values = fieldSet.getValues();
                for (int i = 0; i < values.length; i++) {
                    parseItem.put(columns[i], values[i]);
                }
                return parseItem;
            });
        }});

        return csvFileReader;
    }

    @Bean
    public ListItemWriter<ParsedItem> ramWriter(){
        return new ListItemWriter<>();
    }

    //@Bean
    public JdbcBatchItemWriter<ParsedItem> writer() {
        JdbcBatchItemWriter<ParsedItem> writer = new CustomerJdbcBatchItemWriter<ParsedItem>() {
            @Override
            protected void beforeWrite(List items) {
                if (!CollectionUtils.isEmpty(items)) {

                }
            }
        };
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<ParsedItem>());
        writer.setSql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)");
        writer.setDataSource(dataSource);
        return writer;
    }

    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get("importCsvJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("csvToRam")
                .<ParsedItem, ParsedItem> chunk(3)
                .reader(csvFileItemReader())
//                .processor(processor())
                .writer(ramWriter())
                .build();
    }


}
