import {
  Button,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalHeader,
  ModalOverlay,
  useDisclosure,
} from "@chakra-ui/react";
import UpdateCustomerForm from "./UpdateCustomerForm";

const AddIcon = () => "+";

const UpdateCustomerModal = ({ fetchCustomers, initialValues, customerId }) => {
  const { isOpen, onOpen, onClose } = useDisclosure();

  return (
    <>
      <Button
        bg={"teal.500"}
        color={"white"}
        rounded={"full"}
        _hover={{
          transform: "translateY(-2px)",
          boxShadow: "lg",
        }}
        onClick={onOpen}
      >
        Update customer
      </Button>
      <Modal isOpen={isOpen} onClose={onClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Update customer</ModalHeader>
          <ModalCloseButton />
          <ModalBody pb={6}>
            <UpdateCustomerForm
              closeForm={onClose}
              fetchCustomers={fetchCustomers}
              initialValues={initialValues}
              customerId={customerId}
            />
          </ModalBody>
        </ModalContent>
      </Modal>
    </>
  );
};

export default UpdateCustomerModal;
