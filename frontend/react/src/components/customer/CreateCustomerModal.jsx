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
import CreateCustomerForm from "../shared/CreateCustomerForm";

const AddIcon = () => "+";

const CreateCustomerModal = ({ fetchCustomers }) => {
  const { isOpen, onOpen, onClose } = useDisclosure();

  return (
    <>
      <Button leftIcon={<AddIcon />} colorScheme="teal" onClick={onOpen}>
        Create customer
      </Button>
      <Modal isOpen={isOpen} onClose={onClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Create new customer</ModalHeader>
          <ModalCloseButton />
          <ModalBody pb={6}>
            <CreateCustomerForm
              closeForm={onClose}
              onSuccess={fetchCustomers}
              isSigningUp={false}
            />
          </ModalBody>
        </ModalContent>
      </Modal>
    </>
  );
};

export default CreateCustomerModal;
