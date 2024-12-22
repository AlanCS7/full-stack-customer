import axios from "axios";

const getAuthConfig = () => ({
  headers: {
    Authorization: `Bearer ${localStorage.getItem("access_token")}`,
  },
});

export const getCustomers = async () => {
  try {
    return await axios.get(
      `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`,
      getAuthConfig()
    );
  } catch (error) {
    console.log(error);
    throw error;
  }
};

export const getCustomerByEmail = async (email) => {
  try {
    return await axios.get(
      `${
        import.meta.env.VITE_API_BASE_URL
      }/api/v1/customers/search?email=${email}`,
      getAuthConfig()
    );
  } catch (error) {
    console.log(error);
    throw error;
  }
};

export const createCustomer = async (customer) => {
  try {
    return await axios.post(
      `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`,
      customer
    );
  } catch (error) {
    console.log(error);
    throw error;
  }
};

export const deleteCustomer = async (customerId) => {
  try {
    return await axios.delete(
      `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${customerId}`,
      getAuthConfig()
    );
  } catch (error) {
    console.log(error);
    throw error;
  }
};

export const updateCustomer = async (customerId, customer) => {
  try {
    return await axios.put(
      `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${customerId}`,
      customer,
      getAuthConfig()
    );
  } catch (error) {
    console.log(error);
    throw error;
  }
};

export const login = async (credentials) => {
  try {
    return await axios.post(
      `${import.meta.env.VITE_API_BASE_URL}/api/v1/auth/login`,
      credentials
    );
  } catch (error) {
    console.log(error);
    throw error;
  }
};
