import { Spinner, Text, Wrap, WrapItem } from "@chakra-ui/react";
import { useEffect, useState } from "react";
import CardWithImage from "./components/Card";
import CreateCustomerModal from "./components/CreateCustomerModal";
import SidebarWithHeader from "./components/shared/SideBar";
import { getCustomers } from "./services/client";
import { errorNotification } from "./services/notification";

const App = () => {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchCustomers = async () => {
    setLoading(true);

    try {
      const response = await getCustomers();
      setCustomers(response.data);
    } catch (error) {
      errorNotification(error.response.data.error, error.response.data.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCustomers();
  }, []);

  if (loading) {
    return (
      <SidebarWithHeader>
        <Spinner
          thickness="4px"
          speed="0.65s"
          emptyColor="gray.200"
          color="blue.500"
          size="xl"
        />
      </SidebarWithHeader>
    );
  }

  if (customers.length <= 0) {
    return (
      <SidebarWithHeader>
        <CreateCustomerModal fetchCustomers={fetchCustomers} />
        <Text mt={5}>No customers available</Text>
      </SidebarWithHeader>
    );
  }

  return (
    <SidebarWithHeader>
      <CreateCustomerModal fetchCustomers={fetchCustomers} />
      <Wrap justify={"center"} spacing={"30px"}>
        {customers.map((customer) => (
          <WrapItem key={customer.id}>
            <CardWithImage {...customer} fetchCustomers={fetchCustomers} />
          </WrapItem>
        ))}
      </Wrap>
    </SidebarWithHeader>
  );
};

export default App;
