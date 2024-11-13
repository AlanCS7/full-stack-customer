import {
  Alert,
  AlertIcon,
  Box,
  Button,
  FormLabel,
  Input,
  Stack,
} from "@chakra-ui/react";
import { Form, Formik, useField } from "formik";
import * as Yup from "yup";
import { updateCustomer } from "../services/client";
import {
  errorNotification,
  successNotification,
} from "../services/notification";

const MyTextInput = ({ label, ...props }) => {
  const [field, meta] = useField(props);
  return (
    <Box>
      <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
      <Input className="text-input" {...field} {...props} />
      {meta.touched && meta.error ? (
        <Alert className="error" status="error" mt={2}>
          <AlertIcon />
          {meta.error}
        </Alert>
      ) : null}
    </Box>
  );
};

const UpdateCustomerForm = ({
  closeForm,
  fetchCustomers,
  initialValues,
  customerId,
}) => {
  return (
    <Formik
      initialValues={initialValues}
      validationSchema={Yup.object({
        name: Yup.string()
          .min(3, "Must be at least 3 characters")
          .required("Required"),
        email: Yup.string().email("Invalid email address").required("Required"),
        age: Yup.number()
          .min(16, "Must be at least 16 years of age")
          .max(99, "Must be less than 99 years of age")
          .required("Required"),
      })}
      onSubmit={(updatedCustomer, { setSubmitting }) => {
        setSubmitting(true);
        updateCustomer(customerId, updatedCustomer)
          .then((res) => {
            successNotification(
              "Customer updated",
              `${updatedCustomer.name} was successfully updated`
            );
            closeForm();
            fetchCustomers();
          })
          .catch((error) => {
            errorNotification(
              error.response.data.error,
              error.response.data.message
            );
          })
          .finally(() => {
            setSubmitting(false);
          });
      }}
    >
      {({ isValid, isSubmitting, dirty }) => (
        <Form>
          <Stack spacing={"24px"}>
            <MyTextInput
              label="Name"
              name="name"
              type="text"
              placeholder="John"
            />

            <MyTextInput
              label="Email Address"
              name="email"
              type="email"
              placeholder="john@formik.com"
            />

            <MyTextInput
              label="Age"
              name="age"
              type="number"
              placeholder="16"
            />

            <Box
              sx={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
              }}
            >
              <Button
                type="submit"
                colorScheme="teal"
                disabled={!(isValid && dirty) || isSubmitting}
              >
                Submit
              </Button>
              <Button onClick={closeForm}>Cancel</Button>
            </Box>
          </Stack>
        </Form>
      )}
    </Formik>
  );
};

export default UpdateCustomerForm;
