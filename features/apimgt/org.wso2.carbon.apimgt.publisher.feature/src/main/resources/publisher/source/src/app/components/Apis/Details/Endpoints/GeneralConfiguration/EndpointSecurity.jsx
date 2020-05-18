/* eslint-disable no-unused-vars */
/**
 * Copyright (c)  WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, { useState, useEffect, useContext } from 'react';
import PropTypes from 'prop-types';
import { Grid, TextField, MenuItem } from '@material-ui/core';
import { FormattedMessage, injectIntl } from 'react-intl';
import { isRestricted } from 'AppData/AuthManager';
import { withStyles } from '@material-ui/core/styles';
import APIContext from 'AppComponents/Apis/Details/components/ApiContext';
import APIValidation from 'AppData/APIValidation';
import Alert from 'AppComponents/Shared/Alert';
import Button from '@material-ui/core/Button';
import AddCircle from '@material-ui/icons/AddCircle';
import isEmpty from 'lodash.isempty';
import Table from '@material-ui/core/Table';
import TableHead from '@material-ui/core/TableHead';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableRow from '@material-ui/core/TableRow';
import EditableParameterRow from './EditableParameterRow';

const styles = () => ({
    FormControl: {
        padding: 0,
        width: '100%',
    },
    radioWrapper: {
        display: 'flex',
        flexDirection: 'row',
    },
    addParameter: {
        marginRight: '16px',
    },
    marginRight: {
        marginRight: '8px',
    },
    buttonIcon: {
        marginRight: '16px',
    },
    button: {
        marginTop: '20px',
    },
});

/**
 * The base component for advanced endpoint configurations.
 * @param {any} props The props that are being passed
 * @returns {any} The html representation of the component.
 */
function EndpointSecurity(props) {
    const { api } = useContext(APIContext);
    const {
        intl, securityInfo, onChangeEndpointAuth, isProduction,
        classes,
    } = props;

    const [endpointSecurityInfo, setEndpointSecurityInfo] = useState({
        type: 'BASIC',
        username: '',
        password: '',
        grantType: '',
        tokenUrl: '',
        apiKey: '',
        apiSecret: '',
        customParameters: {},
    });
    const [securityValidity, setSecurityValidity] = useState();

    // Implementation of useState variables for parameter name and value
    const [showAddParameter, setShowAddParameter] = useState(false);
    const [parameterName, setParameterName] = useState(null);
    const [parameterValue, setParameterValue] = useState(null);
    const [editing, setEditing] = useState(false);
    const endpointType = isProduction ? 'production' : 'sandbox';

    const authTypes = [
        {
            key: 'BASIC',
            value: intl.formatMessage({
                id: 'Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.basic',
                defaultMessage: 'Basic Auth',
            }),
        },
        {
            key: 'DIGEST',
            value: intl.formatMessage({
                id: 'Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.digest.auth',
                defaultMessage: 'Digest Auth',
            }),
        },
        {
            key: 'OAUTH',
            value: intl.formatMessage({
                id: 'Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.oauth',
                defaultMessage: 'OAuth',
            }),
        },
    ];
    const grantTypes = [
        {
            key: 'CLIENT_CREDENTIALS',
            value: intl.formatMessage({
                id: 'Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.oauth.grant.type.client',
                defaultMessage: 'Client Credentials',
            }),
        },
        {
            key: 'PASSWORD',
            value: intl.formatMessage({
                id: 'Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.oauth.grant.type.password',
                defaultMessage: 'Password',
            }),
        },
    ];
    useEffect(() => {
        let tmpSecurity = {};
        if (securityInfo !== null) {
            const {
                type, username, password, grantType, tokenUrl, apiKey, apiSecret, customParameters,
            } = securityInfo;
            tmpSecurity.type = type;
            tmpSecurity.username = username;
            tmpSecurity.password = password === '' ? '**********' : password;
            tmpSecurity.grantType = grantType;
            tmpSecurity.tokenUrl = tokenUrl;
            tmpSecurity.apiKey = apiKey;
            tmpSecurity.apiSecret = apiSecret;
            tmpSecurity.customParameters = customParameters;
        }
        setEndpointSecurityInfo(tmpSecurity);
    }, [props]);

    const validateTokenURL = (value) => {
        const state = APIValidation.url.required().validate(value).error;
        // state 'null' means the URL is valid.
        if (state === null) {
            return true;
        } else {
            return false;
        }
    };

    const validateAndUpdateSecurityInfo = (field) => {
        if (!endpointSecurityInfo[field]) {
            setSecurityValidity({ ...securityValidity, [field]: false });
        } else {
            let validity = true;
            if (field === 'tokenUrl') {
                validity = validateTokenURL(endpointSecurityInfo[field]);
            }
            setSecurityValidity({ ...securityValidity, [field]: validity });
        }
        const type = isProduction ? 'production' : 'sandbox';
        onChangeEndpointAuth(endpointSecurityInfo, type);
    };

    const toggleAddParameter = () => {
        setShowAddParameter(!showAddParameter);
    };

    const handleParameterChange = (name) => (event) => {
        const { value } = event.target;
        if (name === 'parameterName') {
            setParameterName(value);
        } else if (name === 'parameterValue') {
            setParameterValue(value);
        }
    };

    const validateEmpty = (itemValue) => {
        if (itemValue === null) {
            return false;
        } else if (itemValue === '') {
            return true;
        } else {
            return false;
        }
    };

    const handleAddToList = () => {
        const customParametersCopy = endpointSecurityInfo.customParameters;

        if (customParametersCopy !== null
            && Object.prototype.hasOwnProperty.call(customParametersCopy, parameterName)) {
            Alert.warning('Parameter name: ' + parameterName + ' already exists');
        } else {
            customParametersCopy[parameterName] = parameterValue;
            setParameterName(null);
            setParameterValue(null);
        }
        setEndpointSecurityInfo({ ...endpointSecurityInfo, customParameters: customParametersCopy });
        onChangeEndpointAuth(endpointSecurityInfo, endpointType);
    };

    const handleUpdateList = (oldRow, newRow) => {
        const customParametersCopy = endpointSecurityInfo.customParameters;
        const { oldName, oldValue } = oldRow;
        const { newName, newValue } = newRow;
        if (customParametersCopy !== null
            && Object.prototype.hasOwnProperty.call(customParametersCopy, newName) && oldName === newName) {
            // Only the value is updated
            if (newValue && oldValue !== newValue) {
                customParametersCopy[oldName] = oldValue;
            }
        } else {
            delete customParametersCopy[oldName];
            customParametersCopy[newName] = newValue;
        }
        setEndpointSecurityInfo({ ...endpointSecurityInfo, customParameters: customParametersCopy });
        onChangeEndpointAuth(endpointSecurityInfo, endpointType);
    };

    const handleDelete = (customParameters, oldName) => {
        const customParametersCopy = endpointSecurityInfo.customParameters;
        if (customParametersCopy !== null && Object.prototype.hasOwnProperty.call(customParametersCopy, oldName)) {
            delete customParametersCopy[oldName];
        }
        setEndpointSecurityInfo({ ...endpointSecurityInfo, customParameters: customParametersCopy });
        onChangeEndpointAuth(endpointSecurityInfo, endpointType);
    };

    const handleKeyDown = (event) => {
        if (event.key === 'Enter') {
            handleAddToList();
        }
    };

    const renderCustomParameters = () => {
        const items = [];
        for (const name in endpointSecurityInfo.customParameters) {
            if (Object.prototype.hasOwnProperty.call(endpointSecurityInfo.customParameters, name)) {
                items.push(<EditableParameterRow
                    oldName={name}
                    oldValue={endpointSecurityInfo.customParameters[name]}
                    handleUpdateList={handleUpdateList}
                    handleDelete={handleDelete}
                    customParameters={endpointSecurityInfo.customParameters}
                    {...props}
                    setEditing={setEditing}
                    isRestricted={isRestricted}
                    api={api}
                />);
            }
        }
        return items;
    };

    return (
        <Grid container direction='row' spacing={2}>
            <Grid item xs={6}>
                <TextField
                    disabled={isRestricted(['apim:api_create'], api)}
                    fullWidth
                    select
                    value={endpointSecurityInfo && endpointSecurityInfo.type}
                    variant='outlined'
                    onChange={(event) => {
                        setEndpointSecurityInfo({
                            ...endpointSecurityInfo,
                            type: event.target.value,
                        });
                    }}
                    inputProps={{
                        name: 'key',
                        id: 'auth-type-select',
                    }}
                    onBlur={() => validateAndUpdateSecurityInfo(isProduction)}
                >
                    {authTypes.map((type) => (
                        <MenuItem value={type.key}>{type.value}</MenuItem>
                    ))}
                </TextField>
            </Grid>
            <Grid item xs={6} />

            {(endpointSecurityInfo.type === 'OAUTH')
                && (
                    <>
                        <Grid item xs={6}>
                            <TextField
                                disabled={isRestricted(['apim:api_create'], api)}
                                required
                                fullWidth
                                select
                                label={(
                                    <FormattedMessage
                                        id={'Apis.Details.Endpoints.GeneralConfiguration'
                                        + '.EndpointSecurity.grant.type.input'}
                                        defaultMessage='Grant Type'
                                    />
                                )}
                                variant='outlined'
                                onChange={(event) => setEndpointSecurityInfo(
                                    { ...endpointSecurityInfo, grantType: event.target.value },
                                )}
                                value={endpointSecurityInfo.grantType}
                                inputProps={{
                                    name: 'key',
                                    id: 'grant-type-select',
                                }}
                                onBlur={() => validateAndUpdateSecurityInfo('grantType')}
                            >
                                {grantTypes.map((type) => (
                                    <MenuItem value={type.key}>{type.value}</MenuItem>
                                ))}
                            </TextField>
                        </Grid>


                        {(endpointSecurityInfo.grantType === 'CLIENT_CREDENTIALS'
                        || endpointSecurityInfo.grantType === 'PASSWORD') && (
                            <>
                                <Grid item xs={6}>
                                    <TextField
                                        disabled={isRestricted(['apim:api_create'], api)}
                                        required
                                        fullWidth
                                        error={securityValidity && securityValidity.tokenUrl === false}
                                        helperText={
                                            securityValidity && securityValidity.tokenUrl === false ? (
                                                <FormattedMessage
                                                    id={'Apis.Details.Endpoints.GeneralConfiguration'
                                            + '.EndpointSecurity.no.tokenUrl.error'}
                                                    defaultMessage={'Token URL should not be empty'
                                                    + ' or formatted incorrectly'}
                                                />
                                            ) : (
                                                <FormattedMessage
                                                    id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                            + 'EndpointSecurity.tokenUrl.message'}
                                                    defaultMessage='Enter Token URL'
                                                />
                                            )
                                        }
                                        variant='outlined'
                                        id='auth-tokenUrl'
                                        label={(
                                            <FormattedMessage
                                                id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                                + 'EndpointSecurity.token.url.input'}
                                                defaultMessage='Token URL'
                                            />
                                        )}
                                        onChange={(event) => setEndpointSecurityInfo(
                                            { ...endpointSecurityInfo, tokenUrl: event.target.value },
                                        )}
                                        value={endpointSecurityInfo.tokenUrl}
                                        onBlur={() => validateAndUpdateSecurityInfo('tokenUrl')}
                                    />
                                </Grid>

                                <Grid item xs={6}>
                                    <TextField
                                        disabled={isRestricted(['apim:api_create'], api)}
                                        required
                                        fullWidth
                                        error={securityValidity && securityValidity.apiKey === false}
                                        helperText={
                                            securityValidity && securityValidity.apiKey === false ? (
                                                <FormattedMessage
                                                    id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                            + 'EndpointSecurity.no.apiKey.error'}
                                                    defaultMessage='API Key should not be empty'
                                                />
                                            ) : (
                                                <FormattedMessage
                                                    id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                                    + 'EndpointSecurity.apiKey.message'}
                                                    defaultMessage='Enter API Key'
                                                />
                                            )
                                        }
                                        variant='outlined'
                                        id='auth-apiKey'
                                        label={(
                                            <FormattedMessage
                                                id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                                + 'EndpointSecurity.api.key.input'}
                                                defaultMessage='API Key'
                                            />
                                        )}
                                        onChange={(event) => setEndpointSecurityInfo(
                                            { ...endpointSecurityInfo, apiKey: event.target.value },
                                        )}
                                        value={endpointSecurityInfo.apiKey}
                                        onBlur={() => validateAndUpdateSecurityInfo('apiKey')}
                                    />
                                </Grid>

                                <Grid item xs={6}>
                                    <TextField
                                        disabled={isRestricted(['apim:api_create'], api)}
                                        required
                                        fullWidth
                                        error={securityValidity && securityValidity.apiSecret === false}
                                        helperText={
                                            securityValidity && securityValidity.apiSecret === false ? (
                                                <FormattedMessage
                                                    id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                            + 'EndpointSecurity.no.apiSecret.error'}
                                                    defaultMessage='API Secret should not be empty'
                                                />
                                            ) : (
                                                <FormattedMessage
                                                    id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                            + 'EndpointSecurity.apiSecret.message'}
                                                    defaultMessage='Enter API Secret'
                                                />
                                            )
                                        }
                                        variant='outlined'
                                        id='auth-apiSecret'
                                        label={(
                                            <FormattedMessage
                                                id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                                + 'EndpointSecurity.api.secret.input'}
                                                defaultMessage='API Secret'
                                            />
                                        )}
                                        onChange={(event) => setEndpointSecurityInfo(
                                            { ...endpointSecurityInfo, apiSecret: event.target.value },
                                        )}
                                        value={endpointSecurityInfo.apiSecret}
                                        onBlur={() => validateAndUpdateSecurityInfo('apiSecret')}
                                    />
                                </Grid>
                            </>
                        )}
                    </>
                )}

            {(endpointSecurityInfo.type === 'BASIC'
                || endpointSecurityInfo.type === 'DIGEST'
                || endpointSecurityInfo.grantType === 'PASSWORD') && (
                <>
                    <Grid item xs={6}>
                        <TextField
                            disabled={isRestricted(['apim:api_create'], api)}
                            required
                            fullWidth
                            error={securityValidity && securityValidity.username === false}
                            helperText={
                                securityValidity && securityValidity.username === false ? (
                                    <FormattedMessage
                                        id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                        + 'EndpointSecurity.no.username.error'}
                                        defaultMessage='Username should not be empty'
                                    />
                                ) : (
                                    <FormattedMessage
                                        id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                        + 'EndpointSecurity.username.message'}
                                        defaultMessage='Enter Username'
                                    />
                                )
                            }
                            variant='outlined'
                            id='auth-userName'
                            label={(
                                <FormattedMessage
                                    id='Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.user.name.input'
                                    defaultMessage='Username'
                                />
                            )}
                            onChange={(event) => setEndpointSecurityInfo(
                                { ...endpointSecurityInfo, username: event.target.value },
                            )}
                            value={endpointSecurityInfo.username}
                            onBlur={() => validateAndUpdateSecurityInfo('username')}
                        />
                    </Grid>

                    <Grid item xs={6}>
                        <TextField
                            disabled={isRestricted(['apim:api_create'], api)}
                            required
                            fullWidth
                            error={securityValidity && securityValidity.password === false}
                            helperText={
                                securityValidity && securityValidity.password === false ? (
                                    <FormattedMessage
                                        id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                        + 'EndpointSecurity.no.password.error'}
                                        defaultMessage='Password should not be empty'
                                    />
                                ) : (
                                    <FormattedMessage
                                        id={'Apis.Details.Endpoints.GeneralConfiguration.'
                                        + 'EndpointSecurity.password.message'}
                                        defaultMessage='Enter Password'
                                    />
                                )
                            }
                            variant='outlined'
                            type='password'
                            id='auth-password'
                            label={(
                                <FormattedMessage
                                    id='Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.password.input'
                                    defaultMessage='Password'
                                />
                            )}
                            value={endpointSecurityInfo.password}
                            onChange={(event) => setEndpointSecurityInfo(
                                { ...endpointSecurityInfo, password: event.target.value },
                            )}
                            onBlur={() => validateAndUpdateSecurityInfo('password')}
                        />
                    </Grid>
                </>
            )}

            {endpointSecurityInfo.type === 'OAUTH'
            && (
                <Grid item xs={6}>
                    <Button
                        size='medium'
                        className={classes.button}
                        onClick={toggleAddParameter}
                        disabled={isRestricted(['apim:api_create', 'apim:api_publish'], api)}
                    >
                        <AddCircle className={classes.buttonIcon} />
                        <FormattedMessage
                            id='Apis.Details.Endpoints.GeneralConfiguration.EndpointSecurity.add.new.parameter'
                            defaultMessage='Add New Parameter'
                        />
                    </Button>
                </Grid>
            )}

            <Grid item xs={12} />

            {(!isEmpty(endpointSecurityInfo.customParameters) || showAddParameter) && (
                <Grid item xs={12}>
                    <Table className={classes.table}>
                        <TableHead>
                            <TableRow>
                                <TableCell>
                                    <FormattedMessage
                                        id={'Apis.Details.Endpoints.GeneralConfiguration'
                                            + '.EndpointSecurity.label.parameter.name'}
                                        defaultMessage='Parameter Name'
                                    />
                                </TableCell>
                                <TableCell>
                                    <FormattedMessage
                                        id={'Apis.Details.Endpoints.GeneralConfiguration'
                                            + '.EndpointSecurity.label.parameter.value'}
                                        defaultMessage='Parameter Value'
                                    />
                                </TableCell>
                                <TableCell />
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {showAddParameter
                            && (
                                <>
                                    <TableRow>
                                        <TableCell>
                                            <TextField
                                                fullWidth
                                                required
                                                id='outlined-required'
                                                label={intl.formatMessage({
                                                    id: 'Apis.Details.Endpoints.GeneralConfiguration'
                                                    + '.EndpointSecurity.input.parameter.name',
                                                    defaultMessage: 'Parameter Name',
                                                })}
                                                margin='normal'
                                                variant='outlined'
                                                className={classes.addParameter}
                                                value={parameterName === null ? '' : parameterName}
                                                onChange={handleParameterChange('parameterName')}
                                                onKeyDown={handleKeyDown('parameterName')}
                                                helperText={validateEmpty(parameterName)
                                                    ? 'Invalid parameter name' : ''}
                                                error={validateEmpty(parameterName)}
                                                disabled={isRestricted(
                                                    ['apim:api_create', 'apim:api_publish'],
                                                    api,
                                                )}
                                            />
                                        </TableCell>
                                        <TableCell>
                                            <TextField
                                                fullWidth
                                                required
                                                id='outlined-required'
                                                label={intl.formatMessage({
                                                    id: 'Apis.Details.Endpoints.GeneralConfiguration'
                                                        + '.EndpointSecurity.input.parameter.value',
                                                    defaultMessage: 'Parameter Value',
                                                })}
                                                margin='normal'
                                                variant='outlined'
                                                className={classes.addParameter}
                                                value={parameterValue === null ? '' : parameterValue}
                                                onChange={handleParameterChange('parameterValue')}
                                                onKeyDown={handleKeyDown('parameterValue')}
                                                error={validateEmpty(parameterValue)}
                                                disabled={isRestricted(
                                                    ['apim:api_create', 'apim:api_publish'],
                                                    api,
                                                )}
                                            />
                                        </TableCell>
                                        <TableCell align='right'>
                                            <Button
                                                variant='contained'
                                                color='primary'
                                                disabled={
                                                    !parameterValue
                                                            || !parameterName
                                                            || isRestricted(
                                                                ['apim:api_create', 'apim:api_publish'], api,
                                                            )
                                                }
                                                onClick={handleAddToList}
                                                className={classes.marginRight}
                                            >
                                                <FormattedMessage
                                                    id='Apis.Details.Properties.Properties.add'
                                                    defaultMessage='Add'
                                                />
                                            </Button>

                                            <Button onClick={toggleAddParameter}>
                                                <FormattedMessage
                                                    id='Apis.Details.Properties.Properties.cancel'
                                                    defaultMessage='Cancel'
                                                />
                                            </Button>
                                        </TableCell>
                                    </TableRow>
                                </>
                            )}
                            {(endpointType === 'production') && (
                                renderCustomParameters()
                            )}
                            {(endpointType === 'sandbox') && (
                                renderCustomParameters()
                            )}
                        </TableBody>
                    </Table>
                </Grid>
            )}
        </Grid>
    );
}

EndpointSecurity.propTypes = {
    intl: PropTypes.func.isRequired,
    securityInfo: PropTypes.shape({}).isRequired,
    onChangeEndpointAuth: PropTypes.func.isRequired,
};

export default withStyles(styles)(injectIntl(EndpointSecurity));
