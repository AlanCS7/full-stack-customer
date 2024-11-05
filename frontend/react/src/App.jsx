import { Spinner, Text, Wrap, WrapItem } from "@chakra-ui/react";
import { useEffect, useState } from "react";
import CardWithImage from "./components/Card";
import SidebarWithHeader from "./components/shared/SideBar";
import { getCustomers } from "./services/client";

const App = () => {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    setCustomers([
      {
        id: 1,
        name: "Ignacio Dietrich",
        email: "ignacio.dietrich@mail.com",
        age: 72,
      },
      {
        id: 2,
        name: "Emanuel Hickle",
        email: "emanuel.hickle@mail.com",
        age: 54,
      },
      {
        id: 3,
        name: "Ashton Funk",
        email: "ashton.funk@mail.com",
        age: 29,
      },
      {
        id: 4,
        name: "Agustin Mayer",
        email: "agustin.mayer@mail.com",
        age: 43,
      },
      {
        id: 5,
        name: "Susann Dickinson",
        email: "susann.dickinson@mail.com",
        age: 43,
      },
      {
        id: 6,
        name: "Julianna Kohler",
        email: "julianna.kohler@mail.com",
        age: 74,
      },
      {
        id: 7,
        name: "Arlena Padberg",
        email: "arlena.padberg@mail.com",
        age: 47,
      },
    ]);
    getCustomers()
      .then((res) => {
        setCustomers(res.data);
      })
      .catch((error) => {
        console.log(error);
      })
      .finally(() => {
        setLoading(false);
      });
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
        <Text>No customers available</Text>
      </SidebarWithHeader>
    );
  }

  return (
    <SidebarWithHeader>
      <Wrap justify={"center"} spacing={"30px"}>
        {customers.map((customer, index) => (
          <WrapItem key={index}>
            <CardWithImage {...customer} />
          </WrapItem>
        ))}
      </Wrap>
    </SidebarWithHeader>
  );
};

export default App;
