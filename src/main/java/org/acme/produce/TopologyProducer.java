package org.acme.produce;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.acme.entity.Product;
import org.acme.model.Aggregation;
import org.acme.model.ItemCart;
import org.acme.model.ProductSell;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.jboss.logging.Logger;

import io.quarkus.kafka.client.serialization.ObjectMapperSerde;

@ApplicationScoped
public class TopologyProducer {

    private static final Logger LOG = Logger.getLogger(TopologyProducer.class);

    private static final String PRODUCT_TOPIC = "products";
    private static final String ITEMS_TOPIC = "items";
    private static final String ORDERS_TOPIC = "orders";
    static final String ITEMS_STORE = "items-store";

    @Inject
    Logger log;

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        ObjectMapperSerde<ProductSell> productSerde = new ObjectMapperSerde<>(
                ProductSell.class);

        ObjectMapperSerde<ItemCart> itemCartSerde = new ObjectMapperSerde<>(
                ItemCart.class);

        ObjectMapperSerde<Aggregation> aggregationSerde = new ObjectMapperSerde<>(Aggregation.class);

        KeyValueBytesStoreSupplier storeSupplier = Stores.persistentKeyValueStore(ITEMS_STORE);

        final GlobalKTable<Long, ProductSell> productTable = builder.globalTable(
                PRODUCT_TOPIC,
                Consumed.with(Serdes.Long(), productSerde));

        final KStream<Long, ItemCart> itemsEvents = builder.stream(
                ITEMS_TOPIC, Consumed.with(Serdes.Long(), itemCartSerde));

        // builder.stream(ITEMS_TOPIC, Consumed.with(Serdes.Long(), itemCartSerde))
        //         .selectKey((k, v) -> v.getProduct().getId())
        //         .join(productTable,
        //                 (productId, itemId) -> productId,
        //                 (item, product) -> item)
        //         .peek((k, v) -> {
        //             log.infof("New: %s", v);
        //             Product build = Product.builder().id(v.getProduct().getId()).title(v.getProduct().getName())
        //                     .description(v.getProduct().getDescription()).build();
        //             Product.persist(build);
        //         });

        builder.stream(ITEMS_TOPIC, Consumed.with(Serdes.Long(), itemCartSerde))
        .selectKey((k, v) -> v.getProductSell().getId())
        .join(productTable,
        (productId, itemId) -> productId,
        (item, product) -> item)
        .peek((k, v) -> {
                log.infof("New: %s", v.getProductSell());
                Product product = new Product();
                product.setId(v.getProductSell().getId());
                product.setTitle(v.getProductSell().getName());
                product.setDescription(v.getProductSell().getDescription());
                // Product build = Product.builder().id(v.getProductSell().getId()).title(v.getProductSell().getName())
                //         .description(v.getProductSell().getDescription()).build();
                Product.persist(product).onFailure().transform(t -> new IllegalStateException(t));
        });
        //.print(Printed.toSysOut());

        // productEvents
        // .peek((key, value) -> System.out.println("Before key=" + key + ", value=" +
        // value))
        // .map((key, value) -> KeyValue.pair(value.id, value))
        // .join(productTable, (productId, itemId) -> productId, (item, product) ->
        // item)
        // .groupByKey()
        // .aggregate(
        // Aggregation::new,
        // (k, v, a) -> {
        // return a;
        // },
        // Materialized.<Integer, Aggregation>as(storeSupplier)
        // .withKeySerde(Serdes.Integer())
        // .withValueSerde(aggregationSerde))
        // .toStream()
        // .peek((k, v) -> log.infof("Done -> %s", v))
        // .to(ORDERS_TOPIC, Produced.with(Serdes.Integer(), aggregationSerde));

        return builder.build();
    }

}
