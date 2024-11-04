import { Button } from "@chakra-ui/react";
import SidebarWithHeader from "./shared/SideBar";

const App = () => {
  return (
    <SidebarWithHeader>
      <Button colorScheme="teal" variant="outline">
        Click me
      </Button>
    </SidebarWithHeader>
  );
};

export default App;
