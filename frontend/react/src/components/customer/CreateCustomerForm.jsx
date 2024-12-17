import {
  Alert,
  AlertIcon,
  Box,
  Button,
  FormLabel,
  Input,
  Select,
  Stack,
} from "@chakra-ui/react";
import { Form, Formik, useField } from "formik";
import * as Yup from "yup";
import { createCustomer } from "../../services/client";
import {
  errorNotification,
  successNotification,
} from "../../services/notification";

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

const MySelect = ({ label, ...props }) => {
  const [field, meta] = useField(props);
  return (
    <Box>
      <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
      <Select {...field} {...props} />
      {meta.touched && meta.error ? (
        <Alert className="error" status="error" mt={2}>
          <AlertIcon />
          {meta.error}
        </Alert>
      ) : null}
    </Box>
  );
};

const CreateCustomerForm = ({ closeForm, fetchCustomers }) => {
  return (
    <Formik
      initialValues={{
        name: "",
        email: "",
        age: "",
        gender: "",
      }}
      validationSchema={Yup.object({
        name: Yup.string()
          .min(3, "Must be at least 3 characters")
          .required("Required"),
        email: Yup.string().email("Invalid email address").required("Required"),
        age: Yup.number()
          .min(16, "Must be at least 16 years of age")
          .max(99, "Must be less than 99 years of age")
          .required("Required"),
        gender: Yup.string()
          .required("Required")
          .oneOf(["MALE", "FEMALE"], "Invalid gender")
          .required("Required"),
      })}
      onSubmit={(customer, { setSubmitting }) => {
        setSubmitting(true);
        createCustomer(customer)
          .then((res) => {
            successNotification(
              "Customer created",
              `${customer.name} was successfully created`
            );
            fetchCustomers();
            closeForm();
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
      {({ isValid, isSubmitting }) => (
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

            <MySelect label="Gender" name="gender">
              <option value="">Select gender</option>
              <option value="MALE">MALE</option>
              <option value="FEMALE">FEMALE</option>
            </MySelect>

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
                disabled={!isValid || isSubmitting}
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

export default CreateCustomerForm;
